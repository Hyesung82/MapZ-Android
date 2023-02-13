package com.cheocharm.presentation.ui.write

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.cheocharm.presentation.R
import com.cheocharm.presentation.base.BaseFragment
import com.cheocharm.presentation.util.AWSUtil
import com.cheocharm.presentation.util.UriUtil
import com.cheocharm.presentation.databinding.FragmentLocationBinding
import com.cheocharm.presentation.ui.MainActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.File
import java.util.*

class LocationFragment : BaseFragment<FragmentLocationBinding>(R.layout.fragment_location), MenuProvider {
    private val pictureViewModel: PictureViewModel by navGraphViewModels(R.id.write)

    private var draggableMarker: Marker? = null
    private var fileName: String? = null
    private var file: File? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val menuHost: MenuHost = requireActivity() as MenuHost
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val picturesAdapter = PicturesAdapter()

        binding.viewmodel = pictureViewModel
        binding.rvLocationPictures.apply {
            adapter = picturesAdapter
        }

        with(binding.toolbarLocation) {
            (activity as MainActivity).setSupportActionBar(this)
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                val action = LocationFragmentDirections.actionLocationFragmentToPictureFragment()
                findNavController().navigate(action)
            }
        }

        val mainActivityBinding = (activity as MainActivity).getBinding()
        mainActivityBinding.fragmentMainMap.isVisible = true

        val mapFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_main_map) as? SupportMapFragment
        mapFragment?.getMapAsync { map ->
            map.setOnMapLoadedCallback {
                pictureViewModel.picture.observe(viewLifecycleOwner) { picture ->
                    picture?.let {
                        picturesAdapter.submitList(listOf(it))

                        activity?.applicationContext?.let { context ->
                            fileName = UUID.randomUUID().toString()
                            file = UriUtil.getFileFromUri(context, it.uri)
                        }

                        val selectedLocation = it.latLng
                        if (selectedLocation != null) {
                            val markerOptions = MarkerOptions()
                                .position(selectedLocation)
                                .draggable(true)
                            draggableMarker = map.addMarker(markerOptions)
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 15F))
                        } else {
                            // TODO: 사진에 장소 정보가 없으면 기본 위치로 카메라 이동
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        draggableMarker?.remove()
        super.onDestroyView()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_base, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.menu_base_confirm -> {
                val action = LocationFragmentDirections.actionLocationFragmentToWriteFragment()
                findNavController().navigate(action)

                activity?.applicationContext?.let { context ->
                    fileName?.let { fn ->
                        file?.let { f ->
                            AWSUtil.uploadWithTransferUtility(context, fn, f)
                        }
                    }
                }

                true
            }
            else -> false
        }
    }
}
