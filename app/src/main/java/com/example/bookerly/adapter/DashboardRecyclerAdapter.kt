package com.example.bookerly.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.bookerly.R
import com.example.bookerly.activity.DescriptionActivity
import com.example.bookerly.activity.DiscriptionActivity
import com.example.bookerly.model.Book
import com.google.android.material.internal.ContextUtils
import com.squareup.picasso.Picasso
import java.security.AccessController.getContext

class DashboardRecyclerAdapter(val context: Context, val itemList: ArrayList<Book>): RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>()
{

    class DashboardViewHolder(view: View): RecyclerView.ViewHolder(view)
    {
//        val textView: TextView = view.findViewById(R.id.tv_rec_row_item_bookname)

        val txtBookName : TextView = view.findViewById(R.id.tv_rec_row_item_bookname)
        val txtBookAuthor : TextView = view.findViewById(R.id.tv_rec_row_item_author)
        val txtBookCost : TextView = view.findViewById(R.id.tv_rec_row_item_price)
        val txtBookRating : TextView = view.findViewById(R.id.txtBookRating)
        val imgBook : ImageView = view.findViewById(R.id.imgBookImage)
        val llContent : LinearLayout = view.findViewById(R.id.llContent)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard_single_row,parent,false)
        return DashboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {

//        val text = itemList[position]
//        holder.textView.text = text

        val book = itemList[position]
        holder.txtBookName.text = book.bookName
        holder.txtBookAuthor.text = book.bookAuthor
        holder.txtBookCost.text = book.bookPrice
        holder.txtBookRating.text = book.bookRating
//        holder.imgBook.setImageResource(book.bookImage)
        Picasso.get().load(book.bookImage).error(R.drawable.default_book_cover).into(holder.imgBook);

        holder.llContent.setOnClickListener{

//            Toast.makeText(context,"U cliked on ${holder.txtBookName.text}",Toast.LENGTH_SHORT).show()
            val intent = Intent(context, DescriptionActivity::class.java)
            intent.putExtra("book_id",book.bookId)
            context.startActivity(intent)
//            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

}