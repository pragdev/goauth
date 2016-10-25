package goauth

import groovy.transform.Canonical
import groovy.transform.TupleConstructor

@Canonical
@TupleConstructor
class Credentials implements Serializable {
  String username
  String password
}
