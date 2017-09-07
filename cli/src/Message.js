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
    let commandSegment = ''
    let usernameSegment = '<' + this.username + '> '
    if ( ['echo', 'broadcast', 'whisper'].indexOf(this.command) > -1 ){
      commandSegment = '(' + this.command + ') '
    }
    if (this.command === 'users'){
      usernameSegment = ''
    }

    return this.timestamp + ' ' + usernameSegment + commandSegment + this.contents
  }
}
