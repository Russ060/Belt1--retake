package com.example.rajeh

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var clRoot: ConstraintLayout
    private lateinit var totalCalories: TextView
    private lateinit var rvCaloriesView: RecyclerView
    private lateinit var intakeTV: EditText
    private lateinit var intakeButton: Button
    private lateinit var burnedTV: EditText
    private lateinit var burnedButton: Button
    private lateinit var dailyCalories: ArrayList<String>
    private lateinit var floatingButton : FloatingActionButton

    private var totalDailyCalories = 0
    private var numericDailyCalories: ArrayList<Int> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initializing RecyclerView variable
        dailyCalories = ArrayList()

        // Setting up the RecyclerView
        rvCaloriesView = findViewById(R.id.rvCalories)
        rvCaloriesView.adapter = RecyclerViewAdapter(dailyCalories)
        rvCaloriesView.layoutManager = LinearLayoutManager(this)

        // intake views
        intakeTV = findViewById(R.id.intakeTV)
        intakeButton = findViewById(R.id.intakeButton)
        intakeButton.setOnClickListener { addCalories() }

        //Burn views
        burnedTV = findViewById(R.id.burnedTV)
        burnedButton = findViewById(R.id.burnedButton)
        burnedButton.setOnClickListener{ burnCalories() }

        // Floating action Button
        floatingButton = findViewById(R.id.floatingActionButton)
        floatingButton.setOnClickListener{ clearRV() }


        sharedPreferences = this.getSharedPreferences(
            getString(R.string.total_calories_file_key), Context.MODE_PRIVATE)
        totalDailyCalories = sharedPreferences.getInt("totalCal", totalDailyCalories)

        // saving the data to the shared preferences
        with(sharedPreferences.edit()) {
            putInt("totalCal", totalDailyCalories)
            apply()
        }


    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.clearEntries -> {
                var dc = dailyCalories.size
                dailyCalories.clear()
                numericDailyCalories = ArrayList()
                totalDailyCalories = numericDailyCalories.sum()
                totalCaloriesTV.text = "Daily Calories: $totalDailyCalories"
                rvCaloriesView.adapter?.notifyDataSetChanged()
                return true
            }
        }
        Toast.makeText(this,"You just deleted all entries",Toast.LENGTH_LONG).show()
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("totalCal", numericDailyCalories.sum())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        totalDailyCalories = savedInstanceState.getInt("totalCal", 0)
        totalCaloriesTV.text = "Daily Calories: $totalDailyCalories"



    }

    // All Functions to be Added under this line
    // Add Calories function
    private fun addCalories() {
        var intakeEntry = intakeTV.text.toString()
        val caloriesLimit = 3000
        if (intakeEntry.toInt() + totalDailyCalories <= caloriesLimit) {
            dailyCalories.add("Calorie Intake: $intakeEntry")
            numericDailyCalories.add(intakeEntry.toInt())
            intakeTV.text.clear()
            intakeTV.clearFocus()
            rvCaloriesView.adapter?.notifyDataSetChanged()
            totalDailyCalories = numericDailyCalories.sum()
            totalCaloriesTV.text = "Daily Calories: $totalDailyCalories"
            Toast.makeText(this,"You entered $intakeEntry calories",Toast.LENGTH_LONG).show()
            scrollDown()
        } else {
            dailyCalories.add("Calorie Intake: $intakeEntry")
            numericDailyCalories.add(intakeEntry.toInt())
            totalDailyCalories = numericDailyCalories.sum()
            totalCaloriesTV.text = "Daily Calories: $totalDailyCalories"
            intakeTV.text.clear()
            intakeTV.clearFocus()
            rvCaloriesView.adapter?.notifyDataSetChanged()
            val difference = totalDailyCalories - caloriesLimit
            dailyCalories.add("You need to burn at least $difference calories to intake more calories")
            Toast.makeText(this,"You must burn $difference calories",Toast.LENGTH_LONG).show()
            disableEntry(intakeTV, intakeButton)
            scrollDown()
        }
    }
    //Burn Calories function
    private fun burnCalories(){
        var burnedEntry = burnedTV.text.toString()
        if (burnedEntry.toInt() > 0 && burnedEntry.toInt() < totalDailyCalories){
            dailyCalories.add("Calorie Burned: $burnedEntry")
            numericDailyCalories.add(burnedEntry.toInt() * -1 )
            totalDailyCalories = numericDailyCalories.sum()
            totalCaloriesTV.text = "Daily Calories: $totalDailyCalories"
            burnedTV.text.clear()
            burnedTV.clearFocus()
            rvCaloriesView.adapter?.notifyDataSetChanged()
            scrollDown()
            Toast.makeText(this,"You burned $burnedEntry calories",Toast.LENGTH_LONG).show()
        }
        if (totalDailyCalories < 3000){
            enableEntry(intakeTV, intakeButton)
        }
        if (burnedEntry.toInt() > totalDailyCalories) {
            Toast.makeText(this,"You cannot burn all the calories in your body, you need some Energy!",Toast.LENGTH_LONG).show()
        }
    }

    //Function to Scroll down the list
    private fun scrollDown() {
    rvCaloriesView.smoothScrollToPosition(dailyCalories.size - 1)
    }

    // Disable Entry Function
    private fun disableEntry(editText: EditText, button: Button) {
        editText.isEnabled = false
        button.isClickable = false
        button.isEnabled = false
        editText.isClickable = false
    }

    // Enable Entry Function
    private fun enableEntry(editText: EditText, button: Button) {
        editText.isEnabled = true
        button.isClickable = true
        button.isEnabled = true
        editText.isClickable = true
    }

    // Function to clear the Recycler View
    private fun clearRV(){
        dailyCalories.removeLast()
        numericDailyCalories.removeLast()
        totalDailyCalories = numericDailyCalories.sum()
        totalCaloriesTV.text = "Daily Calories: $totalDailyCalories"
        Toast.makeText(this,"You just deleted the last entry",Toast.LENGTH_LONG).show()
        rvCaloriesView.adapter?.notifyDataSetChanged()
    }

}