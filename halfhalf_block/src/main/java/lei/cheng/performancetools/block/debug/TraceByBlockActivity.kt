package lei.cheng.performancetools.block.debug

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import lei.cheng.performancetools.block.R

/**
 * @author halflinecode
 * @date 2024/6/16
 * @time 00:03
 */
class TraceByBlockActivity: AppCompatActivity() {

    private val vm by lazy {
        ViewModelProvider(this)[TraceByBlockViewModel::class.java]
    }

    private val recyclerView:RecyclerView by lazy {
        findViewById(R.id.recyclerView)
    }

    private val toolbar:Toolbar by lazy {
        findViewById(R.id.toolbar)
    }

    private val adapter by lazy {
        TraceAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_trace_by_block)
        recyclerView.layoutManager = LinearLayoutManager(this)
        setSupportActionBar(toolbar)
        toolbar.title = "卡顿堆栈详情"
        toolbar.setTitleTextColor(getColor(R.color.white))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        recyclerView.adapter = adapter

        vm.traces.observe(this) {
            adapter.refresh(it)
        }

        val time = intent.getLongExtra(EXTRA_TIME, 0L)
        if (time != 0L) {
            vm.queryTraces(time)
        }
    }

    companion object {
        const val EXTRA_TIME = "extra_time"
        @JvmStatic
        fun launch(context:Context, time:Long) {
            context.startActivity(Intent(context,TraceByBlockActivity::class.java).apply {
                putExtra(EXTRA_TIME, time)
            })
        }
    }
}


class TraceAdapter : RecyclerView.Adapter<TraceItemViewHolder>() {
    private val data = mutableListOf<BlockTraceEntity>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TraceItemViewHolder {
        return TraceItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_item_trace, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: TraceItemViewHolder, position: Int) {
        holder.render(data[position])
    }

    fun refresh(new:List<BlockTraceEntity>) {
        data.clear()
        data.addAll(new)
        this.notifyDataSetChanged()
    }
}

class TraceItemViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
    private val traceText:TextView by lazy {
        itemView.findViewById(R.id.traceText)
    }

    fun render(entity: BlockTraceEntity){
        traceText.text = entity.trace
    }
}