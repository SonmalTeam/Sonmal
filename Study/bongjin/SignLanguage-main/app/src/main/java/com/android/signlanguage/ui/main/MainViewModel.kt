package com.android.signlanguage.ui.main

import androidx.lifecycle.*
import com.android.signlanguage.model.skill.PointsMilestones
import com.android.signlanguage.model.skill.UserSkill

class MainViewModel(private var _userSkill: UserSkill) : ViewModel() {
    var lessonStarted: (() -> Unit)? = null
    var progressReset: (() -> Unit)? = null

    private val _points = MutableLiveData(_userSkill.points)
    val points: LiveData<Int> = _points

    val pointsString: LiveData<String> = Transformations.map(points) {
        it.toString()
    }

    val pointsForNextLevel: LiveData<Int> = Transformations.map(points) {
        PointsMilestones.milestones[PointsMilestones.getClosestIndex(it)!!]
    }
    val pointsForNextLevelString: LiveData<String> = Transformations.map(pointsForNextLevel) {
        " / $it"
    }

    val progressBarPoints: LiveData<Int> = Transformations.map(points) {
        val closestMilestoneIndex = PointsMilestones.getClosestIndex(it)!!
        val closestMilestone = PointsMilestones.milestones[closestMilestoneIndex]
        val previousMilestone =
            if (closestMilestoneIndex - 1 >= 0) PointsMilestones.milestones[closestMilestoneIndex - 1] else 0
        ((it - previousMilestone).toDouble() / (closestMilestone - previousMilestone).toDouble() * 100).toInt()
    }

    val level: LiveData<Int> = Transformations.map(points) {
        PointsMilestones.getClosestIndex(it)!! + 1
    }
    val levelString: LiveData<String> = Transformations.map(level) {
        it.toString()
    }

    fun startLesson() {
        lessonStarted?.invoke()
    }

    fun resetProgress() {
        _userSkill.reset()
        _points.value = _userSkill.points
        progressReset?.invoke()
    }

    fun update() {
        _userSkill = UserSkill.requireInstance()
        _points.value = _userSkill.points
    }
}

class MainViewModelFactory(val userSkill: UserSkill) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(userSkill) as T
    }
}