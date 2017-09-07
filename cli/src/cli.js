import vorpal from 'vorpal'
import { words } from 'lodash'
import { connect } from 'net'
import { Message } from './Message'

export const cli = vorpal()

let argv = require('minimist')(process.argv.slice(2))
let username
let server
let host
let port
let lastCommand
let lastDelimiter
let commandList = ['echo', 'broadcast', 'disconnect']
let command

const getTime = () => {
  let d = new Date()
  let h = d.getHours()
  let m = d.getMinutes()
  let s = d.getSeconds()
  return '' + h + ':' + m + ':' + s
}

const parseInput = (input) => {
  let [parsedCommand, ...rest] = words(input)
  let parsedText = rest.join(' ')
  if (commandList.indexOf(parsedCommand) > -1){
    lastCommand = parsedCommand
  } else {
    parsedText = lastCommand + ' ' + parsedText
  }
  return parsedText
}

cli.delimiter(cli.chalk.yellow(cli.chalk.gray(`${getTime()} `) + 'ftd~$'))

cli
  .mode('connect <username>')
  .delimiter(' ')
  .init(function (args, callback) {
    username = args.username
    lastDelimiter = cli.chalk.gray(`${getTime()} `) + cli.chalk.green(`<${username}>`)
    cli.delimiter(lastDelimiter)
    if (!args.host)
      host = 'localhost'
    if (!args.port)
      port = 8080
    server = connect({ host: host, port: port }, () => {
      server.write(new Message({ username, command: 'connect' }).toJSON() + '\n')
      callback()
    })
    server.on('data', (buffer) => {
      this.log(Message.fromJSON(buffer).toString())
    })
    server.on('end', () => {
      cli.exec('exit')
    })
  })
  .action(function (input, callback) {
    // cli.delimiter(cli.chalk.gray(`${getTime()} `) + cli.chalk.green(`<${username}>`))
    // cli.delimiter(lastDelimiter)
    let [command, ...rest] = words(input)
    let contents = rest.join(' ')
    if (commandList.indexOf(command) === -1){
      contents = command + contents
    } else {
      lastCommand = command
    }
    
    
    if (lastCommand === 'disconnect') {
      this.log('disconnect hit')
      server.end(new Message({ username, lastCommand }).toJSON() + '\n')
    } else if (lastCommand === 'echo'){
      cli.delimiter(cli.chalk.gray(`${getTime()} `) + cli.chalk.green(`<${username}>`) + cli.chalk.red('(echo)'))
      this.log('echo hit')
      server.write(new Message({ username, command: lastCommand, contents }).toJSON() + '\n')      
    } else if (lastCommand === 'broadcast') {
      this.log('broadcast hit')
      server.write(new Message({ username, command: lastCommand, contents }).toJSON() + '\n')    
    }  
    callback()
  })

// cli
//   .mode('echo')
//   .delimiter(cli.chalk.red('(echo)'))
//   .action(function (input, callback) {
//     const [ ...rest ] = words(input)
//     const command = 'echo'
//     const contents = rest.join(' ')

//     if (command === 'disconnect') {
//       server.end(new Message({ username, command }).toJSON() + '\n')
//     } else {
//       server.write(new Message({ username, command, contents }).toJSON() + '\n')
//     }  
//     callback()
//   })
  
  // cli
  // .mode('broadcast', 'sends message to all users')
  // .delimiter(cli.chalk['cyan']('(all)'))
  // .action(function(input, callback){
  //   let [...rest] = words(input)
  //   let command = 'broadcast'
  //   let contents = rest.join(' ')
    
  //   server.write(new Message({ username, command, contents }).toJSON() + '\n')
  //   callback()
  // })




