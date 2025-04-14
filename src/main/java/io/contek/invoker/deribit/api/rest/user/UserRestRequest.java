package io.contek.invoker.deribit.api.rest.user;

import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.rest.RestContext;
import io.contek.invoker.deribit.api.rest.RestRequest;

import javax.annotation.concurrent.NotThreadSafe;


@NotThreadSafe
abstract class UserRestRequest<T> extends RestRequest<T> {

  UserRestRequest(IActor actor, RestContext context) {
    super(actor, context);
    if (actor.getCredential().isAnonymous()) {
      throw new IllegalArgumentException();
    }
  }
}
