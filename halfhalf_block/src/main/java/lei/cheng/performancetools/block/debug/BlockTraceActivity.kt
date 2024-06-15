package lei.cheng.performancetools.block.debug

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import lei.cheng.performancetools.block.R

/**
 * @author halflinecode
 * @date 2024/6/10
 * @time 17:45
 */
class BlockTraceActivity:AppCompatActivity() {
    private val vm by lazy {
        ViewModelProvider(this)[BlockTraceViewModel::class]
    }

    private val recyclerView : RecyclerView by lazy {
        findViewById(R.id.recyclerView)
    }
    private val toolbar : Toolbar by lazy {
        findViewById(R.id.toolbar)
    }

    private val adapter by lazy {
        BlockTraceTimeAdapter { view, item->
            TraceByBlockActivity.launch(this@BlockTraceActivity, item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_block_trace)
        recyclerView.adapter = adapter
        setSupportActionBar(toolbar)
        toolbar.title = "卡顿列表"
        toolbar.setTitleTextColor(getColor(R.color.white))
        supportActionBar?.title = "卡顿列表"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        vm.times.observe(this) {
            adapter.refresh(it)
        }
        vm.queryTimes()
    }
}

class BlockTraceTimeAdapter(val itemClickListener:(View,Long)->Unit) : RecyclerView.Adapter<BlockTraceViewHolder>() {
    private val data = arrayListOf<Long>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockTraceViewHolder {
        return BlockTraceViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_item_trace_time, parent, false),
            itemClickListener
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: BlockTraceViewHolder, position: Int) {
        holder.render(data[position])
    }

    fun refresh(new:List<Long>) {
        data.clear()
        data.addAll(new)
        this.notifyDataSetChanged()
    }
}

class BlockTraceViewHolder(itemView: View, val itemClickListener:(View,Long)->Unit): RecyclerView.ViewHolder(itemView) {
    private val blockTime by lazy {
        itemView.findViewById<TextView>(R.id.blockTime)
    }

    private val container by lazy {
        itemView.findViewById<ConstraintLayout>(R.id.container)
    }

    fun render(time:Long) {
        blockTime.text = time.toString()
        container.setOnClickListener {
            itemClickListener.invoke(it,time)
        }
    }
}