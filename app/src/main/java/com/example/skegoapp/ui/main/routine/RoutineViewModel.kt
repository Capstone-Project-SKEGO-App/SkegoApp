import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.skegoapp.data.pref.Routine

class RoutineViewModel : ViewModel() {

    // LiveData to observe routine data changes
    private val _routineLiveData = MutableLiveData<Routine>()
    val routineLiveData: LiveData<Routine> get() = _routineLiveData

    // Method to update the routine
    fun updateRoutine(routine: Routine) {
        _routineLiveData.value = routine
    }
}
