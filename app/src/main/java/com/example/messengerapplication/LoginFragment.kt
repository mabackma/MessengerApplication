package com.example.messengerapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.messengerapplication.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textViewSignIn: TextView = binding.textSignIn
        textViewSignIn.text = "Welcome!"

        binding.goToRegister.setOnClickListener{
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(action)
        }

        binding.buttonSignIn.setOnClickListener{
            // Otetaan vastaan chat nimi
            val chatname = binding.editTextChatName.text.toString()

            val action = LoginFragmentDirections.actionLoginFragmentToMessageFragment(chatname)
            findNavController().navigate(action)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}