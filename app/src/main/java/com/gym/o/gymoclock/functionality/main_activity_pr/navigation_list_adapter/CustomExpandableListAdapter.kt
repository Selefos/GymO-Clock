package com.gym.o.gymoclock.functionality.main_activity_pr.navigation_list_adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.gym.o.gymoclock.MainActivity
import com.gym.o.gymoclock.R


class CustomExpandableListAdapter(mainActivity: MainActivity, listIcon: List<MenuModel>, listTitle: List<MenuModel>, listChild: HashMap<MenuModel, List<MenuModel>?>) : BaseExpandableListAdapter() {
    private var context: Context? = null
    private var listIcon: List<MenuModel>? = null
    private var listTitle: List<MenuModel>? = null
    private var listItem: Map<MenuModel, List<MenuModel>?>

    init {
        this.context = mainActivity
        this.listIcon = listIcon
        this.listTitle = listTitle
        this.listItem = listChild
    }


    override fun getChild(groupPosition: Int, childPosititon: Int): MenuModel {
        return this.listItem[this.listTitle?.get(groupPosition)]!![childPosititon]
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView
        val childText = getChild(groupPosition, childPosition).menuName
        val childIcon = getChild(groupPosition, childPosition).itemIcon
        if (convertView == null) {
            val inflater =
                context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.list_item, null)
        }

        val childDrawable = convertView?.findViewById<ImageView>(R.id.sub_items_icon)
        childDrawable?.background = childIcon
        val txtListChild = convertView?.findViewById<TextView>(R.id.expandable_list_item)
        txtListChild!!.text = childText

        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return if (this.listTitle?.let { this.listItem[it[groupPosition]] } == null) 0 else this.listItem[this.listTitle!![groupPosition]]!!.size
    }

    override fun getGroup(groupPosition: Int): MenuModel {
        return this.listTitle!![groupPosition]
    }

    override fun getGroupCount(): Int {
        return this.listTitle!!.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        val itemIcon = getGroup(groupPosition).itemIcon
        val headerTitle = getGroup(groupPosition).menuName
        if (convertView == null) {
            val inflater =
                context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.list_group, null)
        }
        val childDrawable = convertView?.findViewById<ImageView>(R.id.group_items_icon)
        childDrawable?.background = itemIcon

        val lblListHeader = convertView!!.findViewById<TextView>(R.id.listTitle)
        lblListHeader.setTypeface(null, Typeface.BOLD)
        lblListHeader.text = headerTitle
        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}