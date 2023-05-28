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
import com.example.messengerapplication.databinding.FragmentMessageBinding
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck
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

    // client-olio, jolla voidaan yhdistää MQTT-brokeriin koodin avulla.
    private lateinit var client: Mqtt3AsyncClient

    // change this to match your fragment name
    private var _binding: FragmentMessageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Asetetaan käyttäjätunnus ja salasana MQTT muuttujiin kirjautumista varten
        HIVEMQ_USERNAME = args.username
        HIVEMQ_PASSWORD = args.password

        buttonSend = binding.buttonSendRemoteMessage
        editTextMessage = binding.remoteMessage

        // Luodaan satunnaisluku client nimeä varten
        var randomNumber = Random.nextInt(0, 100)

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

        // tehdään testinappi, joka lähettää viestin samaan topicciin
        buttonSend.setOnClickListener {
            var stringPayload = HIVEMQ_USERNAME + ":\n" + editTextMessage.text.toString()

            client.publishWith()
                .topic(HIVEMQ_TOPIC)
                .payload(stringPayload.toByteArray())
                .send()

            binding.remoteMessage.text.clear()
            hideKeyboard(editTextMessage)
        }

        return root
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
                    binding.customViewLatest.visibility = View.VISIBLE
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