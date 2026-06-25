package com.example.ui.components

import com.example.R

/**
 * Maps dynamic image resource names from the database into compiled local Android drawable resource IDs.
 * Returns 0 if no match is found, triggering our high-fidelity fallback rendering.
 */
fun getDrawableIdByName(name: String?): Int {
    return when (name) {
        "banner_nexus" -> R.drawable.banner_nexus_1782290597497
        "post_workspace" -> R.drawable.post_workspace_1782290616565
        "post_tech_design" -> R.drawable.post_tech_design_1782290633173
        "story_neon_space" -> R.drawable.story_neon_space_1782291547019
        "story_nature_sun" -> R.drawable.story_nature_sun_1782291564166
        "nexus_logo" -> R.drawable.nexus_logo_1782290910596
        else -> 0
    }
}
