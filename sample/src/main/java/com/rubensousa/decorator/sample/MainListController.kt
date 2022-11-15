/*
 * Copyright (c) 2021. Rúben Sousa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rubensousa.decorator.sample

import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.rubensousa.decorator.DecorationLookup
import com.rubensousa.decorator.LinearBoundsMarginDecoration
import com.rubensousa.decorator.LinearDividerDecoration
import com.rubensousa.decorator.LinearMarginDecoration
import com.rubensousa.decorator.MergeDecorationLookup
import com.rubensousa.decorator.SingleItemDecorationLookup
import com.rubensousa.decorator.sample.adapter.OptionAdapter
import com.rubensousa.decorator.sample.extensions.dpToPx
import com.rubensousa.decorator.sample.model.OptionModel

class MainListController(private val fragment: Fragment) :
    OptionAdapter.OnOptionClickListener {

    private val adapter = OptionAdapter(this)

    fun submitList(list: List<OptionModel>) {
        adapter.submitList(list)
    }

    fun setup(recyclerView: RecyclerView) {
        val edgeDecorationSize = fragment.resources.getDimensionPixelOffset(
            R.dimen.default_edge_decoration_size
        )

        val marginDecorationSize = fragment.resources.getDimensionPixelOffset(
            R.dimen.default_decoration_size
        )

        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.setHasFixedSize(true)

        /**
         * We can compose decorations together
         *
         * 1. Decoration for a top and bottom margin
         * 2. Decoration for item spacing
         * 3. Divider between items
         */
        recyclerView.addItemDecoration(
            LinearBoundsMarginDecoration(
                leftMargin = 0,
                rightMargin = 0,
                topMargin = edgeDecorationSize,
                bottomMargin = edgeDecorationSize
            )
        )

        recyclerView.addItemDecoration(
            LinearMarginDecoration(
                leftMargin = 0,
                rightMargin = 0,
                topMargin = marginDecorationSize,
                bottomMargin = marginDecorationSize,
                decorationLookup = object : DecorationLookup {
                    // We can specify if we don't want to apply a decoration at a given ViewHolder
                    override fun shouldApplyDecoration(
                        viewHolder: ViewHolder, itemCount: Int
                    ): Boolean {
                        return viewHolder.layoutPosition != 4
                    }
                }
            )
        )

        recyclerView.addItemDecoration(
            LinearDividerDecoration.create(
                color = ContextCompat.getColor(fragment.requireActivity(), R.color.colorDivider),
                size = fragment.dpToPx(2),
                leftMargin = fragment.dpToPx(16),
                rightMargin = fragment.dpToPx(72),
                bottomMargin = fragment.dpToPx(16),
                topMargin = fragment.dpToPx(16),
                // We can combine multiple decoration lookups
                decorationLookup = MergeDecorationLookup.of(
                    // Disables decoration when there's only one item
                    SingleItemDecorationLookup(),
                    object : DecorationLookup {
                        override fun shouldApplyDecoration(
                            viewHolder: ViewHolder, itemCount: Int
                        ): Boolean {
                            val layoutPosition = viewHolder.layoutPosition
                            return layoutPosition != 4 && layoutPosition != 3
                        }
                    })
            )
        )

        recyclerView.adapter = adapter
    }

    override fun onOptionClicked(optionModel: OptionModel) {
        val args = DecorationFragmentArgs(optionModel.decorationType, optionModel.titleResource)
        fragment.findNavController().navigate(
            R.id.action_select_option,
            args.toBundle()
        )
    }

}
