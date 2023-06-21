package com.example.messengerapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.messengerapplication.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.buttonRegister.setOnClickListener{
            if(checkPasswd() && binding.editTextUserName.text.toString().isNotBlank()) {
                val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                findNavController().navigate(action)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkPasswd(): Boolean {
        val passwd = binding.editTextPassword.text.toString()
        val passwdCheck = binding.editTextPasswordCheck.text.toString()
        if(passwd.isEmpty() || passwdCheck.isEmpty()) {
            return false
        }
        if(passwd != passwdCheck) {
            binding.textViewMatchingPasswd.visibility = View.VISIBLE
            return false
        }
        return true
    }
}