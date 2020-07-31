package me.ibrahimyilmaz.kiel.datasource

import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import me.ibrahimyilmaz.kiel.adapter.RecyclerDiffCallbackFactory
import me.ibrahimyilmaz.kiel.adapter.RecyclerDiffCallbackFactoryImpl
import me.ibrahimyilmaz.kiel.binder.ViewHolderBinder

class RecyclerDataSource<T : Any>(
    private val renderers: Map<Class<*>, ViewHolderBinder<T>>,
    private val diffCallbackFactory: RecyclerDiffCallbackFactory<T> = RecyclerDiffCallbackFactoryImpl()
) : DataSource<T, RecyclerView.Adapter<*>>(renderers) {

    private val _data = mutableListOf<T>()


    fun getItemViewType(
        position: Int
    ) = requireNotNull(renderers[_data[position].javaClass]?.itemViewType)

    operator fun get(
        position: Int
    ): T = _data[position]

    val count: Int get() = _data.size

    @MainThread
    fun setData(
        value: List<T>
    ) {
        val calculateDiff = DiffUtil.calculateDiff(
            diffCallbackFactory.create(
                _data,
                value
            )
        )

        _data.clear()
        _data += value
        recyclerViewAdapter.get()?.let(calculateDiff::dispatchUpdatesTo)
    }

    @VisibleForTesting
    internal fun seedData(
        data: List<T>
    ) {
        _data.clear()
        _data += data
    }
}