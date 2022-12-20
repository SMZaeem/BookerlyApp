package com.example.bookerly.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.bookerly.R
import com.example.bookerly.adapter.DashboardRecyclerAdapter
import com.example.bookerly.model.Book
import com.example.bookerly.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class DashboardFragment : Fragment() {
    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager:RecyclerView.LayoutManager

//    val bookList = arrayListOf("The Monk Who Sold His Ferrari",
//    "The Alchemist","Power of Subconscious Mind","What Every Body is Saying",
//    "The Subtle art of not giving a fuck","How to win friends and influence people",
//    "How to talk to anyone","Emotional Intelligence","Atomic Habits","Hyper focus",
//    "Indistractable","Think fast and slow","Sapiens","Eat that frog","Ikigai",
//    "Rich Dad Poor Dad","The art of war")

//    val bookInfoList = arrayListOf<Book>(
//        Book("P.S. I love You", "Cecelia Ahern", "Rs. 299", "4.5", R.drawable.ps_ily),
//        Book("The Great Gatsby", "F. Scott Fitzgerald", "Rs. 399", "4.1", R.drawable.great_gatsby),
//        Book("Anna Karenina", "Leo Tolstoy", "Rs. 199", "4.3", R.drawable.anna_kare),
//        Book("Madame Bovary", "Gustave Flaubert", "Rs. 500", "4.0", R.drawable.madame),
//        Book("War and Peace", "Leo Tolstoy", "Rs. 249", "4.8", R.drawable.war_and_peace),
//        Book("Lolita", "Vladimir Nabokov", "Rs. 349", "3.9", R.drawable.lolita),
//        Book("Middlemarch", "George Eliot", "Rs. 599", "4.2", R.drawable.middlemarch),
//        Book("The Adventures of Huckleberry Finn", "Mark Twain", "Rs. 699", "4.5", R.drawable.adventures_finn),
//        Book("Moby-Dick", "Herman Melville", "Rs. 499", "4.5", R.drawable.moby_dick),
//        Book("The Lord of the Rings", "J.R.R Tolkien", "Rs. 749", "5.0", R.drawable.lord_of_rings)
//    )


    val bookInfoList = arrayListOf<Book>()

//    lateinit var btnCheckInternet : Button

    lateinit var recyclerAdapter:DashboardRecyclerAdapter

    lateinit var progressLayout :RelativeLayout
    lateinit var progressBar: ProgressBar

    //functionality added for sorting the books in the dashboard fragment
    var ratingComparator = Comparator<Book>{book1, book2 ->
        if (book1.bookRating.compareTo(book2.bookRating, true) == 0)
        {
            book1.bookName.compareTo(book2.bookName, true)
        }
        else
        {
            book1.bookRating.compareTo(book2.bookRating, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        recyclerDashboard = view.findViewById(R.id.recyclerdashboard)

        setHasOptionsMenu(true)

//        btnCheckInternet= view.findViewById(R.id.btnCheckInternet)

        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility =View.VISIBLE

//        btnCheckInternet.setOnClickListener {
//            if(ConnectionManager().checkConnectivity(activity as Context))
//            {
//                //Internet available
//                val dialog = AlertDialog.Builder(activity as Context)
//                dialog.setTitle("Hurray!")
//                dialog.setMessage("Internet found.")
//                dialog.create()
//                dialog.show()
//
//            }
//            else
//            {
//                //Internet not available
//                val dialog = AlertDialog.Builder(activity as Context)
//                dialog.setTitle("Oops!")
//                dialog.setMessage("Internet not found.")
//                dialog.create()
//                dialog.show()
//            }
//        }

        layoutManager = LinearLayoutManager(activity)



        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v1/book/fetch_books/"

        if(ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest = object :JsonObjectRequest(Method.GET, url, null,Response.Listener
            {
                //here we will handle responses
//            println("Response is $it")
                try {
                    progressLayout.visibility = View.GONE

                    val success = it.getBoolean("success")
                    if(success)
                    {
                        val data = it.getJSONArray("data")
                        for ( i in 0 until data.length())
                        {
                            val bookJsonObject = data.getJSONObject(i)
                            val bookObject = Book(bookJsonObject.getString("book_id"),
                                bookJsonObject.getString("name"),
                                bookJsonObject.getString("author"),
                                bookJsonObject.getString("rating"),
                                bookJsonObject.getString("price"),
                                bookJsonObject.getString("image"))

                            bookInfoList.add(bookObject)
                            recyclerAdapter = DashboardRecyclerAdapter(activity as Context, bookInfoList)

                            recyclerDashboard.adapter = recyclerAdapter
                            recyclerDashboard.layoutManager = layoutManager
                        }
                    }
                    else{
                        Toast.makeText(activity as Context,"some error occured",Toast.LENGTH_SHORT).show()
                    }
                }
                catch (e:JSONException)
                {
                    Toast.makeText(activity as Context,"JSON Exeption occured",Toast.LENGTH_SHORT).show()
                }

            },
                Response.ErrorListener {
                    //here we will handle errors
//                    println("Error is $it")
                    if (activity!=null){
                    Toast.makeText(activity as Context,"Volley Error occured",Toast.LENGTH_SHORT).show()
                        }
                })
            {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String,String>()
                    headers["Context type"] = "application/json"
                    headers["token"] = "a7f4974cafbbb1"
                    return headers
                }

            }

            queue.add(jsonObjectRequest)
        }
        else{
            //Internet not available
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Oops!")
            dialog.setMessage("Internet not found.")
            dialog.setPositiveButton("Open Settings"){ text, listener_ ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()  //we write this so that when we come back to the app after connecting to internet then it recreates the list

            }
            dialog.setNegativeButton("Exit"){text, lilstener->
                ActivityCompat.finishAffinity(activity as Activity) //this one line code let's us finish the app from any point

            }
            dialog.create()
            dialog.show()
        }


        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_dashboard, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item?.itemId
        if (id == R.id.action_sort){
            Collections.sort(bookInfoList, ratingComparator)
            bookInfoList.reverse()
        }
        recyclerAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }

}