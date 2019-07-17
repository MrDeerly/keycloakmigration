package de.klg71.keycloakmigration.changeControl

import de.klg71.keycloakmigration.changeControl.actions.Action
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Execute the keycloakmigration
 */
internal class KeycloakMigration(private val migrationFile: String, realm: String) : KoinComponent {
    private val migrationUserId by inject<UUID>(name = "migrationUserId")
    private val changeFileReader = ChangeFileReader()
    private val changelog = MigrationChangelog(migrationUserId, realm)

    companion object {
        val LOG = LoggerFactory.getLogger(KeycloakMigration::class.java)!!
    }

    internal fun execute() {
        try {
            changeFileReader.changes(migrationFile).let {
                changelog.changesTodo(it)
            }.forEach { change ->
                LOG.info("Executing change: ${change.id}:${change.author}")
                doChange(change)
            }
        } catch (e: Throwable) {
            LOG.error("Migration were unsuccessful see errors above!", e)
        }

    }

    private fun doChange(change: ChangeSet) {
        mutableListOf<Action>().run {
            try {
                change.changes.forEach { action ->
                    action.executeIt()
                    add(action)
                }

                changelog.writeChangesToUser(change)
                LOG.info("Migration ${change.id}:${change.author} Successful executed: $size actions.")
            } catch (e: Exception) {
                LOG.error("Error occurred while migrating: ${e.message} ", e)
                LOG.error("Rolling back changes")
                rollback()
                throw e
            }
        }
    }

    private fun MutableList<Action>.rollback() {
        reverse()
        forEach {
            it.undoIt()
        }
    }
}