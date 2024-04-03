import io.ktor.server.routing.*

fun Routing.setupRoutes() {

    post("/login") {
        // check username, password + 2 FA
    }

    get("/head"){
        // show current Flugleiter
    }

    put("/head/{userId}"){
        // register as current Flugleiter or not
    }

    route("/profile/{userId}") {
        get {
            // show profile page
        }
        post("/change-password") {
            // change password
        }
    }

    route("/flightlogs") {
        route("/{userId}") {
            get {
                // show logs from 1 user
            }
            get("/{id}") {
                // specific protocol
            }
            post("/") {
                // new protocol
            }
            put("/{id}") {
                // change sth in protocol
            }
            delete("/{id}") {
                // delete protocol
            }
            get("/weather"){
                // weather station call up
            }
        }
    }

    route("/admin"){
        get("/flightlogs") {
           // show all flightlogs + opportunity to filter
        }
    }

}
