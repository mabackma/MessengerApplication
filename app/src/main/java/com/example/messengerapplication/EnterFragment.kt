package com.example.messengerapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.messengerapplication.databinding.FragmentEnterBinding
import com.google.firebase.auth.FirebaseAuth


class EnterFragment : Fragment() {

    private var _binding: FragmentEnterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private var signInSuccess: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textViewSignIn: TextView = binding.textEnter
        textViewSignIn.text = "Enter Chat!"

        binding.buttonSignIn.setOnClickListener{
            // Otetaan vastaan chat nimi
            val chatname = binding.editTextChatName.text.toString()

            val action = EnterFragmentDirections.actionEnterFragmentToMessageFragment(chatname)
            findNavController().navigate(action)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}