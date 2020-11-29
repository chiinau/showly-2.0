package com.michaldrabik.network.tmdb.model

data class TmdbActor(
  val id: Long,
  val movieTmdbId: Long,
  val name: String?,
  val character: String?,
  val order: Int,
  val profile_path: String?
)