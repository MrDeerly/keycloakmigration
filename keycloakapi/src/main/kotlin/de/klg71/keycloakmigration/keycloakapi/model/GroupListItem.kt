package de.klg71.keycloakmigration.keycloakapi.model

import java.util.*


data class GroupListItem(val id: UUID,
                         val name: String,
                         val path: String,
                         val subGroupCount: Int)
