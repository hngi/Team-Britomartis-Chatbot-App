package com.britomartis.android.britobudget

import android.content.Intent
import android.content.Intent.*
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.britomartis.android.britobudget.ui.ChatAdapter
import com.britomartis.android.britobudget.utils.Injector
import com.britomartis.android.britobudget.utils.convertTimeLongToDateString
import com.britomartis.android.britobudget.viewmodels.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ChatAdapter.ScrolledFarEnough {

    private val viewModel: MainActivityViewModel by viewModels {
        Injector.provideMainActivityViewModelFactory(this)
    }

    lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chatAdapter = ChatAdapter(this, listOf())
        chat_recyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        chat_recyclerview.adapter = chatAdapter
        chat_recyclerview.setHasFixedSize(true)

        // Observe the list of messages
        viewModel.messageLiveList.observe(this, Observer {
            if (it == null || it.isEmpty()) viewModel.sayFirstHello()
            chatAdapter.dataset = it
            chatAdapter.notifyDataSetChanged()
            chat_recyclerview.scrollToPosition(chatAdapter.dataset.size - 1)
        })

        send_button.setOnClickListener {
            val inputText = userinput_edittext.text?.toString()
            viewModel.sendButtonClicked(inputText)
            userinput_edittext.setText("")
        }

        // Jump to bottom
        fab_quickdown.setOnClickListener {
            chat_recyclerview.scrollToPosition(chatAdapter.dataset.size - 1)
        }

        // Set the date, depending on the first visible item
        chat_recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //Log.d(TAG, "")
                var p =
                    (chat_recyclerview.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                if (p == -1)
                    p = (chat_recyclerview.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                //TODO: Ensure p doesn't become -1

                val message = chatAdapter.dataset[p]
                if (DateUtils.isToday(message.messageTime)) {
                    history_date.visibility = View.GONE
                } else {
                    history_date.visibility = View.VISIBLE
                    history_date.text = convertTimeLongToDateString(message.messageTime)
                }
            }
        })

        more_button.setOnClickListener {
            val popup = PopupMenu(this, it)
            popup.menuInflater.inflate(R.menu.chat_menu_more, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_about -> {
                        Log.d(TAG, "ABOUT")
                        launchAbout()
                        true
                    }
                    R.id.action_feedback -> {
                        Log.d(TAG, "FEEDBACK")
                        launchFeedback()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    private fun launchAbout() {
        startActivity(Intent(this, AboutActivity::class.java))
    }

    private fun launchFeedback() {
        val i = Intent(ACTION_SEND)
        i.type = "message/rfc822"
        i.putExtra(EXTRA_EMAIL, arrayOf("teambritomartis@gmail.com"))
        i.putExtra(EXTRA_SUBJECT, "FeedBack from BritoBot User")
        try {
            startActivity(Intent.createChooser(i, "Send mail..."))
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show()
        }

    }

    override fun scrolledFarEnough(hasScrolledFarEnough: Boolean) {
        if (hasScrolledFarEnough) fab_quickdown.visibility = View.VISIBLE
        else fab_quickdown.visibility = View.GONE
    }

    companion object {
        val TAG = "MainActivity"
    }
}
