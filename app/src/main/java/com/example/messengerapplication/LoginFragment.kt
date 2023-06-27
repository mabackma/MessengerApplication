package com.example.messengerapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.messengerapplication.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textViewSignIn: TextView = binding.textSignIn
        textViewSignIn.text = "Welcome!"

        auth = FirebaseAuth.getInstance()

        binding.goToRegister.setOnClickListener{
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(action)
        }

        binding.buttonSignIn.setOnClickListener{
            val email = binding.editTextUserName.text.toString()
            val password = binding.editTextPassword.text.toString()

            if(!email.isBlank() && !password.isBlank()) {
                signIn(email, password)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    if(auth.currentUser!!.isEmailVerified) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("AUTH", "signInWithEmail:success")
                        Toast.makeText(
                            requireContext(),
                            "Authentication success.",
                            Toast.LENGTH_SHORT,
                        ).show()

                        // Proceed to entering chat.
                        val action = LoginFragmentDirections.actionLoginFragmentToEnterFragment()
                        findNavController().navigate(action)
                    }
                    else {
                        Toast.makeText(
                            requireContext(),
                            "Please verify your email address.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d("AUTH", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        requireContext(),
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }
}