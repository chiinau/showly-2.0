package com.michaldrabik.repository

import com.michaldrabik.data_remote.RemoteDataSource
import com.michaldrabik.data_remote.trakt.model.request.CommentRequest
import com.michaldrabik.repository.mappers.Mappers
import com.michaldrabik.ui_model.Comment
import com.michaldrabik.ui_model.Episode
import com.michaldrabik.ui_model.IdTrakt
import com.michaldrabik.ui_model.Movie
import com.michaldrabik.ui_model.Show
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentsRepository @Inject constructor(
  private val remoteSource: RemoteDataSource,
  private val mappers: Mappers,
  private val userTraktManager: UserTraktManager
) {

  suspend fun loadComments(show: Show, limit: Int = 100) =
    remoteSource.trakt.fetchShowComments(show.traktId, limit)
      .map { mappers.comment.fromNetwork(it) }
      .filter { it.parentId <= 0 }

  suspend fun loadComments(movie: Movie, limit: Int = 100) =
    remoteSource.trakt.fetchMovieComments(movie.traktId, limit)
      .map { mappers.comment.fromNetwork(it) }
      .filter { it.parentId <= 0 }

  suspend fun loadEpisodeComments(idTrakt: IdTrakt, season: Int, episode: Int) =
    remoteSource.trakt.fetchEpisodeComments(idTrakt.id, season, episode)
      .map { mappers.comment.fromNetwork(it) }
      .filter { it.parentId <= 0 }

  suspend fun loadReplies(commentId: Long) =
    remoteSource.trakt.fetchCommentReplies(commentId)
      .map { mappers.comment.fromNetwork(it).copy(replies = 0) }
      .sortedBy { it.createdAt?.toEpochSecond() }

  suspend fun postComment(show: Show, commentText: String, isSpoiler: Boolean): Comment {
    val token = userTraktManager.checkAuthorization().token
    val showBody = mappers.show.toNetwork(Show.EMPTY.copy(ids = show.ids))
    val request = CommentRequest(show = showBody, comment = commentText, spoiler = isSpoiler)

    val comment = remoteSource.trakt.postComment(token, request)
    return mappers.comment.fromNetwork(comment)
  }

  suspend fun postComment(movie: Movie, commentText: String, isSpoiler: Boolean): Comment {
    val token = userTraktManager.checkAuthorization().token
    val movieBody = mappers.movie.toNetwork(Movie.EMPTY.copy(ids = movie.ids))
    val request = CommentRequest(movie = movieBody, comment = commentText, spoiler = isSpoiler)

    val comment = remoteSource.trakt.postComment(token, request)
    return mappers.comment.fromNetwork(comment)
  }

  suspend fun postComment(episode: Episode, commentText: String, isSpoiler: Boolean): Comment {
    val token = userTraktManager.checkAuthorization().token
    val episodeBody = mappers.episode.toNetwork(Episode.EMPTY.copy(ids = episode.ids))
    val request = CommentRequest(episode = episodeBody, comment = commentText, spoiler = isSpoiler)

    val comment = remoteSource.trakt.postComment(token, request)
    return mappers.comment.fromNetwork(comment)
  }

  suspend fun postReply(commentId: Long, commentText: String, isSpoiler: Boolean): Comment {
    val token = userTraktManager.checkAuthorization().token
    val request = CommentRequest(comment = commentText, spoiler = isSpoiler)

    val comment = remoteSource.trakt.postCommentReply(token, commentId, request)
    return mappers.comment.fromNetwork(comment)
  }

  suspend fun deleteComment(commentId: Long) {
    val token = userTraktManager.checkAuthorization().token
    remoteSource.trakt.deleteComment(token, commentId)
  }
}
