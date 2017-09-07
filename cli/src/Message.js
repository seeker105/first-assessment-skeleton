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
    return this.timestamp + ' <' + this.username + '> (' + this.command + ') ' +  this.contents
  }
}
