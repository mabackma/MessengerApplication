package com.example.messengerapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.messengerapplication.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import android.util.Patterns

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = FirebaseAuth.getInstance()

        binding.buttonRegister.setOnClickListener{
            if(isValidEmail()) {
                if(checkPasswd()) {
                    createNewUser()
                }
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
        if(passwd.isBlank() || passwdCheck.isBlank()) {
            return false
        }
        if(passwd != passwdCheck) {
            binding.textViewMatchingPasswd.visibility = View.VISIBLE
            return false
        }
        else {
            binding.textViewMatchingPasswd.visibility = View.INVISIBLE
        }
        return true
    }

    private fun createNewUser() {
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("AUTH", "createUserWithEmail:success")
                    Toast.makeText(
                        requireContext(),
                        "Account created.",
                        Toast.LENGTH_SHORT,
                    ).show()

                    // Move to login fragment
                    val action = RegisterFragmentDirections.actionRegisterFragmentToEnterFragment()
                    findNavController().navigate(action)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d("AUTH", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        requireContext(),
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    binding.textViewValidEmail.visibility = View.VISIBLE
                }
            }
    }

    fun isValidEmail(): Boolean {
        val email = binding.editTextEmail.text.toString()
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isBlank()) {
            binding.textViewValidEmail.visibility = View.VISIBLE
            return false
        }
        else {
            binding.textViewValidEmail.visibility = View.INVISIBLE
        }
        return true
    }
}