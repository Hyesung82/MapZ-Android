package com.cheocharm.presentation.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.cheocharm.domain.model.Error
import com.cheocharm.domain.usecase.group.JoinGroupUseCase
import com.cheocharm.domain.usecase.group.SearchGroupUseCase
import com.cheocharm.presentation.common.Event
import com.cheocharm.presentation.model.GroupModel
import com.cheocharm.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchGroupUseCase: SearchGroupUseCase,
    private val joinGroupUseCase: JoinGroupUseCase
) : ViewModel() {

    private val _searchGroupName = MutableLiveData<String>()
    val searchGroupName: LiveData<String>
        get() = _searchGroupName

    val groupSearchResultList = searchGroupName.switchMap { groupName ->
        searchGroupUseCase.invoke(groupName)
            .map {
                it.map { group -> group.toPresentation() }
            }
            .cachedIn(viewModelScope)
            .asLiveData()
    }

    private val _selectedGroup = MutableLiveData<GroupModel>()
    val selectedGroup: LiveData<GroupModel>
        get() = _selectedGroup

    private val _searchGroupJoinBottom = MutableLiveData<Event<Unit>>()
    val searchGroupJoinBottom: LiveData<Event<Unit>>
        get() = _searchGroupJoinBottom

    private val _toastMessage = MutableLiveData<Event<String>>()
    val toastMessage: LiveData<Event<String>>
        get() = _toastMessage

    fun setSearchGroupName(groupName: String) {
        _searchGroupName.value = groupName
    }

    fun setSelectedGroup(group: GroupModel) {
        _selectedGroup.value = group
    }

    fun joinGroup() {
        viewModelScope.launch {
            selectedGroup.value?.name?.let {
                joinGroupUseCase.invoke(it)
                    .onSuccess {
                        _searchGroupJoinBottom.value = Event(Unit)
                    }.onFailure { throwable ->
                        when (throwable) {
                            is Error.JoinGroupUnavailable -> setToastMessage(throwable.message)
                            else -> setToastMessage("그룹 가입 요청을 실패하였습니다.")
                        }
                    }
            }
        }
    }

    private fun setToastMessage(message: String) {
        _toastMessage.value = Event(message)
    }
}
