package tt.co.jesses.familiar

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
