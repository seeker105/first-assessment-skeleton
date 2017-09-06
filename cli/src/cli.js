import vorpal from 'vorpal'
import { words } from 'lodash'
import { connect } from 'net'
import { Message } from './Message'

export const cli = vorpal()

let username
let server
let servers = {}
let host
let port

cli
  .delimiter(cli.chalk['yellow']('ftd~$'))

cli
  .mode('connect <username>')
  .delimiter(cli.chalk['green']('connected>'))
  .init(function (args, callback) {
    username = args.username
    if (!args.host)
      host = 'localhost'
    if (!args.port)
      port = 8080
    server = connect({ host: host, port: port }, () => {
      server.write(new Message({ username, command: 'connect' }).toJSON() + '\n')
      callback()
    })
    servers[username] = server
    server.on('data', (buffer) => {
      this.log(Message.fromJSON(buffer).toString())
    })

    server.on('end', () => {
      cli.exec('exit')
    })
  })
  .action(function (input, callback) {
    const [ command, ...rest ] = words(input)
    const contents = rest.join(' ')

    if (command === 'disconnect') {
      server.end(new Message({ username, command }).toJSON() + '\n')
    } else if (command === 'echo') {
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else {
      this.log(`Command <${command}> was not recognized`)
    }

    callback()
  })

cli
  .mode('broadcast')
  .delimiter(cli.chalk['cyan']('<' + username + '>'))
  .action(function (input, callback) {
    const [command, ...rest] = words(input)
    let contents = rest.join(' ')
    if (command === 'disconnect') {
      server.end(new Message({ username, command }).toJSON() + '\n')
    }  else if (command === 'echo') {
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else {
      contents = command + ' ' + contents 
      for (let eachServer of servers) {
        server.write(new Message({ username, command, contents }).toJSON() + '\n')
      }
    }
  })



cli
  .catch('[words...]', 'Catches incorrect commands')
  .action(function (args, callback) {
    this.log('Command <' + args.words[0] + '> was not recognized')
    callback()
  })
