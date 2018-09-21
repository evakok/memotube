package misproject.memotube

class Memo {

    var position: Long? = null
    var timestamp: String? = null
    var imgFile: String? = null

    constructor(position: Long, title: String, content: String) {
        this.position = position
        this.timestamp = title
        this.imgFile = content
    }

}