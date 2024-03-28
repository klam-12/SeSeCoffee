package com.example.sesecoffee

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast

class ProductOrderActivity : AppCompatActivity() {
    private var productName = "Capuccicno"
    private var quantity = 1
    private var price = 3.0
    private var sizeFee = 0.0
    private var milkFee = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_order)

        val productNameTextView = findViewById<TextView>(R.id.orderItem)
        val quantityTextView = findViewById<TextView>(R.id.orderQuantity)
        val priceTextView = findViewById<TextView>(R.id.orderPrice)
        productNameTextView.setText(productName)
        quantityTextView.setText("$quantity")
        priceTextView.setText("$${price * quantity}")

        val hotColdRadioGroup = findViewById<RadioGroup>(R.id.orderHotColdChoice)
        val sizeRadioGroup = findViewById<RadioGroup>(R.id.orderSizeChoice)
        val milkRadioGroup = findViewById<RadioGroup>(R.id.orderMilkChoice)

        val smallSizeRadio = findViewById<RadioButton>(R.id.smallSize)
        val mediumSizeRadio = findViewById<RadioButton>(R.id.mediumSize)
        val largeSizeRadio = findViewById<RadioButton>(R.id.largeSize)
        smallSizeRadio.setOnClickListener {
            addSizeFee(0.0)
            findViewById<TextView>(R.id.orderPrice).setText("$${price * quantity}")
        }
        mediumSizeRadio.setOnClickListener {
            addSizeFee(1.0)
            findViewById<TextView>(R.id.orderPrice).setText("$${price * quantity}")
        }
        largeSizeRadio.setOnClickListener {
            addSizeFee(2.0)
            findViewById<TextView>(R.id.orderPrice).setText("$${price * quantity}")
        }

        val noMilkRadio = findViewById<RadioButton>(R.id.noMilk)
        val smallMilkRadio = findViewById<RadioButton>(R.id.smallMilk)
        val largeMilkRadio = findViewById<RadioButton>(R.id.largeMilk)
        noMilkRadio.setOnClickListener {
            addMilkFee(0.0)
            findViewById<TextView>(R.id.orderPrice).setText("$${price * quantity}")
        }
        smallMilkRadio.setOnClickListener {
            addMilkFee(1.0)
            findViewById<TextView>(R.id.orderPrice).setText("$${price * quantity}")
        }
        largeMilkRadio.setOnClickListener {
            addMilkFee(2.0)
            findViewById<TextView>(R.id.orderPrice).setText("$${price * quantity}")
        }

        findViewById<Button>(R.id.orderQuantityPlus).setOnClickListener {
            quantity++
            quantityTextView.setText("$quantity")
            priceTextView.setText("$${price * quantity}")
        }

        findViewById<Button>(R.id.orderQuantityMinus).setOnClickListener {
            quantity--
            if(quantity < 1){
                quantity = 1
            }
            quantityTextView.setText("$quantity")
            priceTextView.setText("$${price * quantity}")
        }

        findViewById<Button>(R.id.orderNextBtn).setOnClickListener {
            val hotColdChoice = hotColdRadioGroup.checkedRadioButtonId
            val sizeChoice = sizeRadioGroup.checkedRadioButtonId
            val milkChoice = milkRadioGroup.checkedRadioButtonId

            if (sizeChoice == -1 || milkChoice == -1 || hotColdChoice == -1) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val quantity = quantityTextView.text.toString().toInt()
            val totalPrice = price * quantity
            val temperature = resources.getResourceEntryName(hotColdChoice)
            val size = resources.getResourceEntryName(sizeChoice)
            val milk = resources.getResourceEntryName(milkChoice)

            Toast.makeText(this, "Order: $quantity $totalPrice $temperature $size $milk", Toast.LENGTH_SHORT).show()

            val intent = Intent(
                applicationContext,
                CartOrderActivity::class.java
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.orderBackBtn).setOnClickListener {
            finish()
        }

        findViewById<ImageButton>(R.id.orderCartBtn).setOnClickListener {
            val intent = Intent(
                applicationContext,
                CartOrderActivity::class.java
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    fun addSizeFee(fee: Double): Double {
        price -= sizeFee
        sizeFee = fee
        price += sizeFee
        return price * quantity
    }

    fun addMilkFee(fee: Double): Double {
        price -= milkFee
        milkFee = fee
        price += milkFee
        return price * quantity
    }
}