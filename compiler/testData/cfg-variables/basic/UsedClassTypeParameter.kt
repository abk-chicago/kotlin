fun println(t: Any?) = t

class UsedClassTypeParameter<T>(t: T) {
    {
        println(t)
    }
}
