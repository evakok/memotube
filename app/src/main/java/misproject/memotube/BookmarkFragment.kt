package misproject.memotube

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.io.File
import java.util.*

class BookmarkFragment : Fragment() {

    private var videoTitle = "DEMO"

    private var listMemos = ArrayList<Memo>()
    private lateinit var listView : ListView

    private lateinit var adapter: MemoAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //TODO
        // when item clicked - overlay on top of the video (reposition the video play position)
        val view: View = inflater!!.inflate(R.layout.activity_drawer, container, false)
        listView = view.findViewById(R.id.bookmarkList) as ListView

        return view
    }

    fun addMemoItems() {

        var filePath = ""
        var timestamp = ""
        var time : Int = 0

        var initialMemos = ArrayList<Memo>()

        var dirPath = File(Environment.getExternalStorageDirectory().absolutePath + File.separator + "Memotube")
        val allFiles = dirPath.listFiles()

        if (allFiles != null && allFiles.size > 0) {
            for (file in allFiles) {
                if (file.name.startsWith(videoTitle) && file.name.endsWith(".png")) {
                    // File absolute path
                    filePath = file.getAbsolutePath()
                    // File Name
                    timestamp = file.getName()
                    timestamp = timestamp.removePrefix(videoTitle)
                    timestamp = timestamp.removeSuffix(".png")
                    time = timestamp.toInt()

                    val seconds = (time / 1000) % 60
                    val minutes = (time / (1000 * 60) % 60)
                    val hours = (time / (1000*60*60))
                    timestamp = String.format("%01d:%02d:%02d", hours, minutes, seconds)

                    initialMemos.add(Memo(time.toLong(), timestamp, filePath))
                }
            }
            var sortedList = initialMemos.sortedWith(compareBy({ it.position }))
            if (sortedList != null) {
                listMemos.addAll(sortedList)
            }
        }
    }

    inner class MemoAdapter : BaseAdapter {

        private var memosList = ArrayList<Memo>()
        private var context: Context? = null

        private var inflater: LayoutInflater? = null

        constructor(context: Context, memosList: ArrayList<Memo>) : super() {
            this.memosList = memosList
            this.context = context
            this.inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        override fun getCount(): Int {
            return memosList.size
        }

        //2
        override fun getItem(position: Int): Any {
            return memosList[position]
        }

        //3
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        //4
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            // Get view for row item
            val view: View = inflater!!.inflate(R.layout.list_memo, parent, false)

            // Get title element
            val title = view.findViewById(R.id.title) as TextView
            // Get thumbnail element
            val thumbnail = view.findViewById(R.id.thumbnail) as ImageView

            val memo = getItem(position) as Memo

            title.text = memo.timestamp
            thumbnail.setImageBitmap(BitmapFactory.decodeFile(memo.imgFile))

            return view
        }
    }

    fun updateListview() {

        videoTitle = getArguments()!!.getString("title")
        listMemos.clear()
        addMemoItems()

        adapter = MemoAdapter(this.activity!!, listMemos)
        listView.adapter = adapter
    }
}