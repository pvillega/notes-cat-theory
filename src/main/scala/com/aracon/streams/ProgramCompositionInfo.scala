/*
 * Copyright 2018 Pere Villega
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aracon.streams

object ProgramCompositionInfo {

  // Notes based on talk of Fabio Lavella about 'Compose your program flow with Stream'
  // Talk is about declarative program flows

  // Pure FP is about purity not as a quality but as a property: referential transparency
  // That is you can replace an expression by its final value without changing the result
  // A 'side effect' breaks referential transparency

  // FS2 Stream: list on steroids. A few constructors, many composition operations
  // Declare operations without evaluating them

  // Declarative control flow
  // Create simple single actions in IO, join them using Stream
  // See Streams as a logical thread of operations, isolated, without worrying about concurrency
  // Enable concurrent logical threads by interleaving streams, without having to implement complex concurrency behaviour, done for you
  // For example via 'Stream.concurrently' operation

  // Remember 'flatMap' is sequential! Better to do 'map' + 'join' to run Steam[Stream] concurrently.

  // FS2 provides 'Signal'. Tracks changes over an 'A'.
  // Along 'Stream.sleep' (and other combinators) allows us to 'signal' changes or specific statuses in a Stream

}
