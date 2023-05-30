package com.example.messengerapplication

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.navigation.fragment.navArgs
import com.example.messengerapplication.database.Message
import com.example.messengerapplication.database.MessageDAO
import com.example.messengerapplication.databinding.FragmentMessageBinding
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random


class MessageFragment : Fragment() {
    val HIVEMQ_BROKER = "4d14ef6101be488bbc5604f9bc853ff9.s2.eu.hivemq.cloud"
    private lateinit var HIVEMQ_USERNAME: String
    private lateinit var HIVEMQ_PASSWORD: String
    val HIVEMQ_TOPIC = "chatTopic"

    // get fragment parameters from previous fragment
    val args: MessageFragmentArgs by navArgs()

    private lateinit var buttonSend: Button
    private lateinit var editTextMessage: EditText
    private lateinit var buttonClear: Button

    // client-olio, jolla voidaan yhdistää MQTT-brokeriin koodin avulla.
    private lateinit var client: Mqtt3AsyncClient

    // change this to match your fragment name
    private var _binding: FragmentMessageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Tämä luodaan vain kerran, silloin kun sitä tarvitaan
    private val messageDAO: MessageDAO by lazy {
        MessageDAO(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Näytetään kaikki viestit tietokannasta
        val messages = messageDAO.getAllMessages()
        displayMessages(messages)

        // Asetetaan käyttäjätunnus ja salasana MQTT muuttujiin kirjautumista varten.
        HIVEMQ_USERNAME = args.username
        HIVEMQ_PASSWORD = args.password

        buttonSend = binding.buttonSendRemoteMessage
        editTextMessage = binding.remoteMessage
        buttonClear = binding.buttonClear

        // Luodaan satunnaisluku client nimeä varten
        var randomNumber = Random.nextInt(0, 10000)

        client = MqttClient.builder()
            .useMqttVersion3()
            .sslWithDefaultConfig()
            .identifier("android2023test" + randomNumber)
            .serverHost(HIVEMQ_BROKER)
            .serverPort(8883)
            .buildAsync()

        // yhdistetään käyttäjätiedoilla (username/password)
        client.connectWith()
            .simpleAuth()
            .username(HIVEMQ_USERNAME)
            .password(HIVEMQ_PASSWORD.toByteArray())
            .applySimpleAuth()
            .send()
            .whenComplete { connAck: Mqtt3ConnAck?, throwable: Throwable? ->
                if (throwable != null) {
                    Log.d("ADVTECH", "Connection failure.")
                } else {
                    // Setup subscribes or start publishing
                    subscribeToTopic()
                }
            }

        // nappi, joka lähettää viestin
        buttonSend.setOnClickListener {
            var stringPayload = "@" + HIVEMQ_USERNAME + ": " + editTextMessage.text.toString()

            client.publishWith()
                .topic(HIVEMQ_TOPIC)
                .payload(stringPayload.toByteArray())
                .send()

            binding.remoteMessage.text.clear()
            hideKeyboard(editTextMessage)
        }

        buttonClear.setOnClickListener {
            messageDAO.deleteAllMessages()
            binding.customViewLatest.clearData()
        }

        return root
    }

    // Näytetään kaikki tietokannassa olevat viestit.
    private fun displayMessages(messages: List<Message>) {
        var index = 0
        while (index < messages.size) {
            val message = messages[index]
            binding.customViewLatest.addData(message.content)
            index++
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        // suljetaan MQTT-yhteys mikäli fragment suljetaan
        client.disconnect()
    }

    fun subscribeToTopic()
    {
        client.subscribeWith()
            .topicFilter(HIVEMQ_TOPIC)
            .callback { publish ->

                // this callback runs everytime your code receives new data payload
                var result = String(publish.getPayloadAsBytes())

                activity?.runOnUiThread {
                    binding.customViewLatest.addData(result)

                    // Aikaleima
                    val currentDateTime = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val formattedTimestamp = currentDateTime.format(formatter)

                    // Luodaan Message.
                    val msg = Message(
                        id = null,
                        timestamp = formattedTimestamp,
                        content = result
                    )
                    // Lisätään message tietokantaan.
                    messageDAO.insertMessage(msg)
                }

            }
            .send()
            .whenComplete { subAck, throwable ->
                if (throwable != null) {
                    // Handle failure to subscribe
                    Log.d("ADVTECH", "Subscribe failed.")
                } else {
                    // Handle successful subscription, e.g. logging or incrementing a metric
                    Log.d("ADVTECH", "Subscribed!")
                }
            }
    }

    fun Fragment.hideKeyboard(editText: EditText) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }
}