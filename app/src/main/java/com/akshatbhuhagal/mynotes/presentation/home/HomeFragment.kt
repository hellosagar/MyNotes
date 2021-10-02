package com.akshatbhuhagal.mynotes.presentation.home

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.akshatbhuhagal.mynotes.R
import com.akshatbhuhagal.mynotes.database.NotesDataBase
import com.akshatbhuhagal.mynotes.databinding.FragmentHomeBinding
import com.akshatbhuhagal.mynotes.entities.Notes
import com.akshatbhuhagal.mynotes.presentation.create_notes.CreateNoteFragment
import com.akshatbhuhagal.mynotes.util.BaseFragment
import com.akshatbhuhagal.mynotes.util.viewBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : BaseFragment() {

    private val binding by viewBinding(FragmentHomeBinding::bind)

    var arrNotes = ArrayList<Notes>()
    var notesAdapter: NotesAdapter = NotesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        launch {
            context?.let {

                val notes = NotesDataBase.getDataBase(it).noteDao().getAllNotes()
                notesAdapter.setData(notes)
                arrNotes = notes as ArrayList<Notes>
                binding.recyclerView.adapter = notesAdapter
            }
        }

        notesAdapter.setOnClickListener(onClicked)

        // Find View By ID
        val fabCreateNoteBtn = view.findViewById<FloatingActionButton>(R.id.fabCreateNoteBtn)

        // FAB CREATE NOTE FRAGMENT
        fabCreateNoteBtn.setOnClickListener {
            replaceFragment(CreateNoteFragment.newInstance(), true)
        }

        search_view.setOnQueryTextListener(object :
                SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(p0: String?): Boolean {

                    val tempArr = ArrayList<Notes>()

                    for (arr in arrNotes) {
                        if (arr.title!!.toLowerCase(Locale.getDefault()).contains(p0.toString())) {
                            tempArr.add(arr)
                        }
                    }
                    notesAdapter.setData(tempArr)
                    notesAdapter.notifyDataSetChanged()
                    return true
                }
            })
    }

    private val onClicked = object : NotesAdapter.OnItemClickListener {
        override fun onClicked(notesId: Int) {

            val fragment: Fragment
            val bundle = Bundle()
            bundle.putInt("noteId", notesId)
            fragment = CreateNoteFragment.newInstance()
            fragment.arguments = bundle

            replaceFragment(fragment, true)
        }
    }

    fun replaceFragment(fragment: Fragment, istransition: Boolean) {

        val fragmentTransition = requireActivity().supportFragmentManager.beginTransaction()

        if (istransition) {
            fragmentTransition.setCustomAnimations(
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left
            )
        }
        fragmentTransition.replace(R.id.flFragmenet, fragment)
            .addToBackStack(fragment.javaClass.simpleName)
        fragmentTransition.commit()
    }
}