export class Message {
  static fromJSON (buffer) {
    return new Message(JSON.parse(buffer.toString()))
  }

  constructor ({ timestamp, username, command, contents, target }) {
    this.username = username
    this.command = command
    this.contents = contents
    this.target = target
    this.timestamp = timestamp
  }

  toJSON () {
    return JSON.stringify({
      username: this.username,
      command: this.command,
      contents: this.contents,
      target: this.target,
      timestamp: this.timestamp
    })
  }

  toString () {
    let result = this.timestamp
    if (this.command === 'users'){
      result = result + ': ' + this.contents
    } else if (this.command === 'connect' || this.command === 'disconnect') {
      result = result + ': <' + this.username + '> ' + this.contents
    } else if (this.command === 'broadcast') {
      result = result + ' <' + this.username + '> (all): ' + this.contents      
    } else {
      result = result + ' <' + this.username + '> (' + this.command + '): ' + this.contents
    }
    return result
  }
}
