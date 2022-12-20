package com.example.bookerly.activity

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.Volley
import com.example.bookerly.R
import com.example.bookerly.database.BookDatabase
import com.example.bookerly.database.BookEntity
import com.squareup.picasso.Picasso
import org.json.JSONObject

class DescriptionActivity : AppCompatActivity() {

    lateinit var txtBookName : TextView
    lateinit var txtBookAuthor : TextView
    lateinit var txtBookPrice : TextView
    lateinit var txtBookRating : TextView
    lateinit var txtBookDesc : TextView
    lateinit var imgBookImage : ImageView
    lateinit var btnAddToFav : Button
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout : RelativeLayout
    lateinit var toolbar: androidx.appcompat.widget.Toolbar

    var bookId : String? = "100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        txtBookName = findViewById(R.id.txtBookName)
        txtBookAuthor = findViewById(R.id.txtBookAuthor)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookRating = findViewById(R.id.txtBookRating)
        txtBookDesc = findViewById(R.id.txtBookDescription)
        imgBookImage = findViewById(R.id.imgBookImage)
        txtBookDesc = findViewById(R.id.txtBookDescription)
        btnAddToFav = findViewById(R.id.btnAddToFav)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        progressLayout = findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE

         toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Book Details"

        if(intent!=null)
        {
            bookId = intent.getStringExtra("book_id")
        }
        else
        {
            finish()
            Toast.makeText(this@DescriptionActivity,"Some unexpected error occurred!", Toast.LENGTH_SHORT).show()
        }
        if(bookId=="100")
        {
            finish()
            Toast.makeText(this@DescriptionActivity,"Some unexpected error occurred!", Toast.LENGTH_SHORT).show()
        }

        val queue = Volley.newRequestQueue(this@DescriptionActivity)
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

                    //code for db --------------
                    val bookImageUrl = bookJsonObject.getString("image")
                    //----------------

                    Picasso.get().load(bookJsonObject.getString("image")).error(R.drawable.default_book_cover).into(imgBookImage)
                    txtBookName.text = bookJsonObject.getString("name")
                    txtBookAuthor.text = bookJsonObject.getString("author")
                    txtBookPrice.text = bookJsonObject.getString("price")
                    txtBookRating.text = bookJsonObject.getString("rating")
                    txtBookDesc.text = bookJsonObject.getString("description")

                    //code for db --------------
                    val bookEntity = BookEntity(
                        bookId?.toInt() as Int,
                        txtBookName.text.toString(),
                        txtBookAuthor.text.toString(),
                        txtBookPrice.text.toString(),
                        txtBookRating.text.toString(),
                        txtBookDesc.text.toString(),
                        bookImageUrl
                    )
                    val checkFav = DBAsyncTask(applicationContext, bookEntity, 1).execute()
                    val isFav = checkFav.get()

                    if (isFav)
                    {
                        btnAddToFav.text = "Remove from Favourites"
                        val favColor = ContextCompat.getColor(applicationContext, R.color.removefromfav)
                        btnAddToFav.setBackgroundColor(favColor)
                    }
                    else
                    {
                        btnAddToFav.text = "Add to Favourites"
                        val noFavColor = ContextCompat.getColor(applicationContext, R.color.black)
                        btnAddToFav.setBackgroundColor(noFavColor)
                    }


                    btnAddToFav.setOnClickListener {
                        if (!DBAsyncTask(applicationContext, bookEntity, 1).execute().get()){
                            val async = DBAsyncTask(applicationContext, bookEntity, 2).execute()
                            val result = async.get()
                            if (result)
                            {
                                Toast.makeText(this@DescriptionActivity, "book added to fav", Toast.LENGTH_SHORT).show()
                                btnAddToFav.text = "Remove from Favourites"
                                val favColor = ContextCompat.getColor(applicationContext, R.color.removefromfav)
                                btnAddToFav.setBackgroundColor(favColor)
                            }
                            else
                            {
                                Toast.makeText(this@DescriptionActivity, "some error occurred!!",Toast.LENGTH_SHORT).show()
                            }
                        }
                        else{
                            val async = DBAsyncTask(applicationContext, bookEntity, 3).execute()
                            val result = async.get()

                            if (result)
                            {
                                Toast.makeText(this@DescriptionActivity, "book removed from fav", Toast.LENGTH_SHORT).show()
                                btnAddToFav.text = "Add to Favourites"
                                val noFavColor = ContextCompat.getColor(applicationContext, R.color.black)
                                btnAddToFav.setBackgroundColor(noFavColor)
                            }
                            else{
                                Toast.makeText(this@DescriptionActivity, "some error occurred!!",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }


                }
                else{
                    Toast.makeText(this@DescriptionActivity,"Some unexpected error occurred!", Toast.LENGTH_SHORT).show()
                }

            }
            catch (e:Exception){
                Toast.makeText(this@DescriptionActivity,"Some unexpected error occurred!", Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener {
            Toast.makeText(this@DescriptionActivity,"Volley error occurred!", Toast.LENGTH_SHORT).show()
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
    class DBAsyncTask(val context: Context, val bookEntity: BookEntity, val mode: Int) : AsyncTask<Void, Void, Boolean>(){

        //            mode 1-> check db if the book is favourite or not
//            mode 2-> save the book in the db as favourite
//            mode 3-> remove the book from favourite
        val db = Room.databaseBuilder(context, BookDatabase::class.java, "book-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            when(mode)
            {
                1 -> {
//                        check db if the book is favourite or not
                    val book: BookEntity? = db.bookDao().getBookById(bookEntity.book_id.toString())
                    db.close()
                    return book != null
                }
                2 -> {
//                         mode 2-> save the book in the db as favourite
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true
                }
                3 -> {
//                        mode 3-> remove the book from favourite
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true
                }
            }
            return false
        }

    }
}