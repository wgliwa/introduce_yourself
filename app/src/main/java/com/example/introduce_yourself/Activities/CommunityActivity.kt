package com.example.introduce_yourself.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.introduce_yourself.Models.ReadInvitationsModel
import com.example.introduce_yourself.Models.ReadUserModel
import com.example.introduce_yourself.Models.UserPostModel
import com.example.introduce_yourself.R
import com.example.introduce_yourself.adapters.UserEditPostsAdapter
import com.example.introduce_yourself.adapters.UserInvitationsAdapter
import com.example.introduce_yourself.database.Friend
import com.example.introduce_yourself.database.Friends
import com.example.introduce_yourself.database.User
import com.example.introduce_yourself.utils.currentUser
import com.example.introduce_yourself.utils.getCommunityList
import com.recyclerviewapp.UsersList
import kotlinx.android.synthetic.main.activity_community.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class CommunityActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val FRIEND_DETAILS = "user_details"
    }

    private var friendsList = ArrayList<ReadUserModel>()
    private var invitationsList = ArrayList<ReadUserModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)

        setSupportActionBar(toolbar_community)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar_community.setNavigationOnClickListener {
            finish()
        }

        friendsList = getCommunityList(currentUser!!.id.value, 1)
        friendsRecyclerView(friendsList)

        community_friends_list_button.setOnClickListener(this)
        community_invitations_list_button.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.community_friends_list_button -> {
                if (community_friends_rv.visibility == View.GONE) {
                    community_friends_rv.visibility = View.VISIBLE
                    community_invitations_rv.visibility = View.GONE
                    Log.e("community", "friends")
                    friendsList.clear()
                    friendsList = getCommunityList(currentUser!!.id.value, 1)
                    friendsRecyclerView(friendsList)
                }
            }
            R.id.community_invitations_list_button -> {
                if (community_invitations_rv.visibility == View.GONE) {
                    community_friends_rv.visibility = View.GONE
                    community_invitations_rv.visibility = View.VISIBLE
                    Log.e("community", "inv")
                    invitationsList.clear()
                    invitationsList = getCommunityList(
                        currentUser!!.id.value,
                        0
                    ) //todo Mateusz ReadInvitationsModel -> ReadUserModel
                    usersInvitationsRecyclerView(invitationsList)
                }
            }
        }
    }

    private fun friendsRecyclerView(readUserModelList: ArrayList<ReadUserModel>) {
        community_friends_rv.layoutManager = LinearLayoutManager(this)
        community_friends_rv.setHasFixedSize(true)
        val usersList = UsersList(this, readUserModelList)
        community_friends_rv.adapter = usersList

        usersList.setOnClickListener(object : UsersList.OnClickListener {
            override fun onClick(position: Int, model: ReadUserModel) {
                val intent = Intent(
                    this@CommunityActivity,
                    UserItemActivity::class.java
                )

                intent.putExtra(
                    FRIEND_DETAILS,
                    model
                )

                startActivity(intent)
            }
        })
    }

    private fun usersInvitationsRecyclerView(readUserInvitationsModelList: ArrayList<ReadUserModel>) {
        community_invitations_rv.layoutManager = LinearLayoutManager(this)
        community_invitations_rv.setHasFixedSize(true)
        val userInvitationsList = UserInvitationsAdapter(this, readUserInvitationsModelList)
        community_invitations_rv.adapter = userInvitationsList

        userInvitationsList.setOnClickListener(
            object : UserInvitationsAdapter.OnClickListener {
                override fun onClick(position: Int, model: ReadUserModel) {
                    Log.e("index", position.toString())
                }
            },
            object : UserInvitationsAdapter.OnAcceptClickListener {
                override fun onClick(position: Int, model: ReadUserModel) {
                    Log.e("accept index ", position.toString())
                    changeInvitationStatus(model, 1)
                }
            },
            object : UserInvitationsAdapter.OnRejectClickListener {
                override fun onClick(position: Int, model: ReadUserModel) {
                    Log.e("reject index ", position.toString())
                    changeInvitationStatus(model, -1)
                }
            }
        )
    }

    private fun changeInvitationStatus(model: ReadUserModel, new: Int) =
        runBlocking {
            newSuspendedTransaction(Dispatchers.IO) {
                val x =
                    Friend.find { (Friends.to eq currentUser!!.id) and (Friends.from eq model.id) }
                        .firstOrNull()
                if (x != null)
                    when (new) {
                        1 -> x.status = 1
                        -1 -> x.delete()
                    }
            }
        }

}