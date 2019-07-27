package de.klg71.keycloakmigration.changeControl.actions.group

import de.klg71.keycloakmigration.AbstractIntegrationTest
import de.klg71.keycloakmigration.changeControl.actions.MigrationException
import de.klg71.keycloakmigration.model.GroupListItem
import de.klg71.keycloakmigration.rest.KeycloakClient
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import org.koin.core.inject
import java.util.*

class AddGroupIntegTest : AbstractIntegrationTest() {

    val client by inject<KeycloakClient>()

    @Test
    fun testAddGroup() {
        AddGroupAction(testRealm, "integrationTest").executeIt()

        GroupListItem(UUID.randomUUID(), "integrationTest", "integrationTest", listOf()).let {
            assertThat(client.searchGroup("integrationTest", testRealm)).hasSize(1).usingElementComparatorOnFields("name").contains(it)
        }
    }

    @Test
    fun testAddGroupAlreadyExisting() {
        AddGroupAction(testRealm, "integrationTest").executeIt()
        assertThatThrownBy {
            AddGroupAction(testRealm, "integrationTest").executeIt()
        }.isInstanceOf(MigrationException::class.java)
                .hasMessage("Group with name: integrationTest already exists in realm: ${testRealm}!")
    }
}