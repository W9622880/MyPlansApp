/*
Copyright 2022 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.example.myica.model.service.impl

import com.example.myica.model.Plan
import com.example.myica.model.service.AccountService
import com.example.myica.model.service.StorageService
import com.example.myica.model.service.trace
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import javax.inject.Inject
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await

class StorageServiceImpl
@Inject
constructor(private val firestore: FirebaseFirestore, private val auth: AccountService) :
    StorageService {

  override val plans: Flow<List<Plan>>
    get() =
      auth.currentUser.flatMapLatest { user ->
        currentCollection(user.id)
          .snapshots()
          .map { snapshot -> snapshot.toObjects() }
      }

    override suspend fun getPlan(planId: String): Plan? =
        currentCollection(auth.currentUserId).document(planId).get().await().toObject()

    override suspend fun save(plan: Plan): String =
        trace(SAVE_PLAN_TRACE) { currentCollection(auth.currentUserId).add(plan).await().id }

    override suspend fun update(plan: Plan): Unit =
        trace(UPDATE_PLAN_TRACE) {
            currentCollection(auth.currentUserId).document(plan.id).set(plan).await()
        }

    override suspend fun delete(planId: String) {
        currentCollection(auth.currentUserId).document(planId).delete().await()
    }

    // TODO: It's not recommended to delete on the client:
    // https://firebase.google.com/docs/firestore/manage-data/delete-data#kotlin+ktx_2
    override suspend fun deleteAllForUser(userId: String) {
        val matchingTasks = currentCollection(userId).get().await()
        matchingTasks.map { it.reference.delete().asDeferred() }.awaitAll()
    }

    private fun currentCollection(uid: String): CollectionReference =
        firestore.collection(USER_COLLECTION).document(uid).collection(PLAN_COLLECTION)

    companion object {
        private const val USER_COLLECTION = "users"
        private const val PLAN_COLLECTION = "plans"
        private const val SAVE_PLAN_TRACE = "savePlan"
        private const val UPDATE_PLAN_TRACE = "updatePlan"
    }
}
