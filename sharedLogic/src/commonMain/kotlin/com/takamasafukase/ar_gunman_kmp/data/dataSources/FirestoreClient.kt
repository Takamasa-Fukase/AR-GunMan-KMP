package com.takamasafukase.ar_gunman_kmp.data.dataSources

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

interface FirestoreClientInterface {
    suspend fun <R : Any> getItems(collectionPath: String, serializer: KSerializer<R>): List<R>
    suspend fun <R : Any> addItem(collectionPath: String, request: R, serializer: KSerializer<R>)
}

suspend inline fun <reified R : Any> FirestoreClientInterface.getItems(collectionPath: String): List<R> =
    getItems(collectionPath, serializer<R>())

suspend inline fun <reified R : Any> FirestoreClientInterface.addItem(
    collectionPath: String,
    request: R
) =
    addItem(collectionPath, request, serializer<R>())

class FirestoreClient : FirestoreClientInterface {
    private val db = Firebase.firestore

    override suspend fun <R : Any> getItems(
        collectionPath: String,
        serializer: KSerializer<R>
    ): List<R> {
        return try {
            db.collection(collectionPath)
                .get()
                .documents
                .map { it.data(serializer) }
        } catch (error: Exception) {
            throw error
        }
    }

    override suspend fun <R : Any> addItem(
        collectionPath: String,
        request: R,
        serializer: KSerializer<R>
    ) {
        try {
            db.collection(collectionPath)
                .add(serializer, request)
        } catch (error: Exception) {
            throw error
        }
    }
}