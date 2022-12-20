package com.example.bookerly.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.android.volley.ExecutorDelivery
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.Volley
import com.example.bookerly.R
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import org.json.JSONObject

class DiscriptionActivity : AppCompatActivity() {

    lateinit var txtBookName : TextView
    lateinit var txtBookAuthor : TextView
    lateinit var txtBookPrice : TextView
    lateinit var txtBookRating : TextView
    lateinit var txtBookDesc : TextView
    lateinit var imgBookImage : ImageView
    lateinit var btnAddToFav : Button
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout : RelativeLayout

     var bookId : String? = "random"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discription)

        txtBookName = findViewById(R.id.txtBookName)
        txtBookAuthor = findViewById(R.id.txtBookAuthor)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookRating = findViewById(R.id.txtBookRating)
        txtBookDesc = findViewById(R.id.txtBookDescription)
        imgBookImage = findViewById(R.id.imgBookImage)
        btnAddToFav = findViewById(R.id.btn_addToFav)
        progressBar = findViewById(R.id.progressBar)
        progressLayout = findViewById(R.id.rlProgressLayout)

        progressLayout.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE

        if(intent!=null)
        {
            bookId = intent.getStringExtra("book_id")
        }
        else
        {
            finish()
            Toast.makeText(this@DiscriptionActivity,"Some unexpected error occurred!", Toast.LENGTH_SHORT).show()
        }
        if(bookId=="random")
        {
            finish()
            Toast.makeText(this@DiscriptionActivity,"Some unexpected error occurred!", Toast.LENGTH_SHORT).show()
        }

        val queue = Volley.newRequestQueue(this@DiscriptionActivity)
        val url = "http://13.235.250.119/v1/book/get_book/"
        val jsonParams = JSONObject()
        jsonParams.put("book_id",bookId)
        val jsonRequest = object: JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

        try {
            val success = it.getBoolean("success")
            if(success)
            {
                val bookJsonObject = it.getJSONObject("book_data")
                progressLayout.visibility=View.GONE

                Picasso.get().load(bookJsonObject.getString("image")).error(R.drawable.default_book_cover).into(imgBookImage)
                txtBookName.text = bookJsonObject.getString("name")
                txtBookAuthor.text = bookJsonObject.getString("author")
                txtBookPrice.text = bookJsonObject.getString("price")
                txtBookRating.text = bookJsonObject.getString("rating")
                txtBookDesc.text = bookJsonObject.getString("description")
            }
            else{
                Toast.makeText(this@DiscriptionActivity,"Some unexpected error occurred!", Toast.LENGTH_SHORT).show()
            }

        }
        catch (e:Exception){
            Toast.makeText(this@DiscriptionActivity,"Some unexpected error occurred!", Toast.LENGTH_SHORT).show()
        }
        }, Response.ErrorListener {
            Toast.makeText(this@DiscriptionActivity,"Volley error occurred!", Toast.LENGTH_SHORT).show()
        }){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String,String>()
                headers["Content-type"] = "application/json"
                headers["token"] = "a7f4974cafbbb1"
                return headers
            }

        }
        queue.add(jsonRequest)



    }
}