package com.example.cashmachine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.cashmachine.dao.SummaryDao
import com.example.cashmachine.databinding.FragmentCreditBinding
import com.example.cashmachine.utils.Constants
import com.example.cashmachine.utils.Functions
import kotlinx.coroutines.launch


class CreditFragment : Fragment() {
    private lateinit var binding: FragmentCreditBinding


    //    private val db = FirebaseFirestore.getInstance()
    private val viewModel: CashViewModel by activityViewModels()

    private var selectedMode: String? = null

    private val denominations = listOf(500, 200, 100, 50, 20, 10)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreditBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun updateToggleUI() {
        binding.toggleCredit.isSelected = selectedMode == Constants.TYPE_CREDIT
        binding.toggleDebit.isSelected = selectedMode == Constants.TYPE_DEBIT


        binding.toggleCredit.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (selectedMode == Constants.TYPE_CREDIT) R.color.black else R.color.unselected
            )
        )
        binding.toggleDebit.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (selectedMode == Constants.TYPE_DEBIT) R.color.black else R.color.unselected
            )
        )
    }

    private fun setupUI() {

        binding.toggleCredit.setOnClickListener {
            selectedMode = Constants.TYPE_CREDIT
            updateToggleUI()
        }
        binding.toggleDebit.setOnClickListener {
            selectedMode = Constants.TYPE_DEBIT
            updateToggleUI()
        }

        binding.btnSubmit.setOnClickListener { validateAndSubmit() }
    }

    private fun validateAndSubmit() {
        val amountText = binding.editTextAmount.text.toString()

        if (Functions.isFieldEmpty(amountText)) {
            Functions.showErrorSnackbar(binding.root, "Please enter amount")
            return
        }

        if (selectedMode == null) {
            Functions.showErrorSnackbar(
                binding.root,
                "Please select Credit or Debit"
            )
            return
        }

        if (!Functions.isValidAmount(amountText)) {
            Functions.showErrorSnackbar(
                binding.root,
                "Amount must be positive and multiple of ₹10"
            )
            return
        }
        val amount = amountText.toInt()
//        if (selectedMode == Constants.TYPE_CREDIT) {
//            // distributeAndSave(amount)
//            return
//        }
        if (selectedMode == Constants.TYPE_CREDIT) {
            creditAmount(amount)
        } else {
            debitAmount(amount)
        }

        // validateDebit(amount)
    }
    private fun creditAmount(amount: Int) {
        val breakdown = calculateBreakdown(amount) // Map<Int, Int>

        lifecycleScope.launch {
            viewModel.credit(
                a500 = breakdown[500]?.toLong() ?: 0L,
                a200 = breakdown[200]?.toLong() ?: 0L,
                a100 = breakdown[100]?.toLong() ?: 0L,
                a50  = breakdown[50]?.toLong() ?: 0L,
                a20  = breakdown[20]?.toLong() ?: 0L,
                a10  = breakdown[10]?.toLong() ?: 0L
            )

            Functions.showErrorSnackbar(binding.root, "Amount Credited")
            binding.editTextAmount.text?.clear()
        }
    }

    private fun debitAmount(amount: Int) {


        val breakdown = calculateBreakdown(amount)

  val totalBalance = viewModel.summary.value?.total ?: 0

        if (totalBalance < amount) {
            Functions.showErrorSnackbar(
                binding.root,
                "Insufficient balance"
            )
            return
        }

        for ((den, countNeeded) in breakdown) {


        }



        lifecycleScope.launch {
            val debitAmount = viewModel.debit(
                a500 = breakdown[500]?.toLong() ?: 0L,
                a200 = breakdown[200]?.toLong() ?: 0L,
                a100 = breakdown[100]?.toLong() ?: 0L,
                a50  = breakdown[50]?.toLong() ?: 0L,
                a20  = breakdown[20]?.toLong() ?: 0L,
                a10  = breakdown[10]?.toLong() ?: 0L
            )

            Functions.showErrorSnackbar(binding.root, "$debitAmount Debited")
        }
    }

    private fun calculateBreakdown(amount: Int): Map<Int, Int> {
        val result = mutableMapOf<Int, Int>()
        var remaining = amount

        for (den in denominations) {
            val count = remaining / den
            if (count > 0) {
                result[den] = count
                remaining %= den
            }
        }

        return result
    }






    /*
        private fun validateDebit(amount: Int) {
            val summaryRef = db.collection("denominations").document("summary")

            summaryRef.get().addOnSuccessListener { snapshot ->

                val total = snapshot.getLong("total") ?: 0L

                if (total < amount) {
                    Functions.showErrorSnackbar(
                        binding.root,
                        "Insufficient balance"
                    )
                    return@addOnSuccessListener
                }

                val required = calculateBreakdown(amount)

                for ((den, countNeeded) in required) {
                    val available = snapshot.getLong(den.toString()) ?: 0L
                    if (countNeeded > available) {
                        Functions.showErrorSnackbar(
                            binding.root,
                            "Not enough ₹$den notes available"
                        )
                        return@addOnSuccessListener
                    }
                }

                distributeAndSave(amount)
            }
        }
     */
    /*
    private fun distributeAndSave(amount: Int) {

        val denominationsCount = calculateBreakdown(amount)

        binding.textviewSummary.text = denominationsCount.entries.joinToString("\n") {
            "₹${it.key} × ${it.value}"
        }

        val summaryRef = db.collection("denominations").document("summary")


        val transactionData = hashMapOf(
            Constants.TYPE_KEY to selectedMode,
            Constants.AMOUNT_KEY to amount,
            Constants.TIMESTAMP_KEY to com.google.firebase.firestore.FieldValue.serverTimestamp(),
            Constants.DENOMINATIONS_KEY to denominationsCount.mapKeys { it.key.toString() }
        )

        db.runTransaction { transaction ->

            val snap = transaction.get(summaryRef)
            val updatedMap = mutableMapOf<String, Any>()

            for (den in denominations) {
                val existing = snap.getLong(den.toString()) ?: 0L
                val change = (denominationsCount[den] ?: 0).toLong()

                val newValue =
                    if (selectedMode == Constants.TYPE_CREDIT) existing + change
                    else existing - change

                updatedMap[den.toString()] = newValue.coerceAtLeast(0L)
            }

            val total = denominations.sumOf { den ->
                (updatedMap[den.toString()] as Long) * den
            }

            updatedMap["total"] = total

            transaction.set(
                db.collection("transactions").document(),
                transactionData
            )

            transaction.set(summaryRef, updatedMap, SetOptions.merge())

        }.addOnSuccessListener {

            if (!isAdded) return@addOnSuccessListener

            Functions.showErrorSnackbar(
                binding.root,
                "Transaction successful"
            )

            binding.editTextAmount.text?.clear()

        }.addOnFailureListener { e ->

            if (!isAdded) return@addOnFailureListener

            Functions.showErrorSnackbar(
                binding.root,
                "Transaction failed: ${e.message}"
            )
        }
    }
     */
}







