export class Message {
  static fromJSON (buffer) {
    return new Message(JSON.parse(buffer.toString()))
  }

  constructor ({ username, command, contents, target }) {
    this.username = username
    this.command = command
    this.contents = contents
    this.target = target
  }

  toJSON () {
    return JSON.stringify({
      username: this.username,
      command: this.command,
      contents: this.contents,
      target: this.target
    })
  }

  toString () {
    return this.contents
  }
}
