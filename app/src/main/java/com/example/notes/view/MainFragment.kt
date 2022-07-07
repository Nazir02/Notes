package com.example.notes.view


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.adapter.MainAdapter
import com.example.notes.databinding.FragmentMainBinding
import com.example.notes.model.NoteModel
import com.example.notes.vm.MainViewModel

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MainAdapter


    companion object {
        var clik =0
        @JvmStatic
        fun clikTitle(noteModel: NoteModel) {
            val bundle = Bundle()
            bundle.putSerializable("note", noteModel)
            MAIN.navController.navigate(R.id.action_mainFragment_to_addFragment, bundle)
                 clik=1
        }


    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        init()

    }

    private fun init() {
        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.initDatabase()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView = binding.recyclerView
        adapter = MainAdapter()
        recyclerView.adapter = adapter
        viewModel.getallNote().observe(viewLifecycleOwner) { listNotes ->
            adapter.setList(listNotes.asReversed())

        }
        binding.flatingActionButton.setOnClickListener {
            MAIN.navController.navigate(R.id.action_mainFragment_to_addFragment)

        }
    }


}