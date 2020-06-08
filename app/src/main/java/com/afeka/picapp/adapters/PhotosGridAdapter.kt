import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.afeka.picapp.OnPhotoClickListener
import com.afeka.picapp.R
import com.squareup.picasso.Picasso

class PhotosGridAdapter(private val c: Context, private val images: List<String>, private val onPhotoClickListener: OnPhotoClickListener) :
    RecyclerView.Adapter<PhotosGridAdapter.ColorViewHolder>() {


    override fun getItemCount(): Int {
        return images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        return ColorViewHolder(LayoutInflater.from(c).inflate(R.layout.item_grid_kotlin, parent, false))
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val path = images[position]

        Picasso.get()
            .load(path)
            .resize(250, 250)
            .centerCrop()
            .into(holder.iv)

        holder.iv.setOnClickListener {
            onPhotoClickListener.clicked(path)
        }
    }

    class ColorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iv = view.findViewById(R.id.iv) as ImageView
    }
}