package com.github.cleverage.elasticsearch

import org.elasticsearch.client.Client

import concurrent.{Future, Promise}
import org.elasticsearch.action.{ActionResponse, ActionRequest, ActionListener, ActionRequestBuilder}
import play.libs.F

/**
 * Utils for managing Asynchronous tasks
 */
object AsyncUtils {
  /**
   * Create a default promise
   * @return
   */
  def createPromise[T](): Promise[T] = Promise[T]()

  /**
   * Execute an Elasticsearch request asynchronously
   * @param requestBuilder
   * @return
   */
  def executeAsync[RQ <: ActionRequest[RQ],RS <: ActionResponse, RB <: ActionRequestBuilder[RQ,RS,RB,CL], CL <: Client](requestBuilder: ActionRequestBuilder[RQ,RS,RB,CL]): Future[RS] = {
    val promise = Promise[RS]()

    requestBuilder.execute(new ActionListener[RS] {
      def onResponse(response: RS) {
        promise.success(response)
      }

      def onFailure(t: Throwable) {
        promise.failure(t)
      }
    })

    promise.future
  }

  /**
   * Execute an Elasticsearch request asynchronously and return a Java Promise
   * @param requestBuilder
   * @return
   */
  def executeAsyncJava[RQ <: ActionRequest[RQ],RS <: ActionResponse, RB <: ActionRequestBuilder[RQ,RS,RB,CL], CL <: Client](requestBuilder: ActionRequestBuilder[RQ,RS,RB,CL]): F.Promise[RS] = {
    F.Promise.wrap(executeAsync(requestBuilder))
  }

}
