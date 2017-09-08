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
let commandList = ['echo', 'broadcast', 'disconnect', 'users']
let command
let target
let contents

const colorTable = {
  'connect': 'green',
  'disconnect': 'gray',
  'echo': 'red',
  'broadcast': 'blue',
  'whisper': 'yellow',
  'users': 'magenta'
}

const getTime = () => {
  let d = new Date()
  let h = d.getHours()
  let m = d.getMinutes()
  let s = d.getSeconds()
  return '' + h + ':' + m + ':' + s
}

// const parseInput = (input) => {
//   let [parsedCommand, ...rest] = words(input)
//   let parsedText = rest.join(' ')
//   if (commandList.indexOf(parsedCommand) > -1){
//     lastCommand = parsedCommand
//   } else {
//     parsedText = lastCommand + ' ' + parsedText
//   }
//   return parsedText
// }

cli.delimiter(cli.chalk.yellow('ftd~$'))

cli
  .mode('connect <username>')
  .delimiter(cli.chalk.green('connected> '))
  .init(function (args, callback) {
    username = args.username
    lastDelimiter = cli.chalk.yellow('ftd~$') + cli.chalk.green(` <${username}>`)
    cli.delimiter(lastDelimiter)
    if (!args.host)
      host = 'localhost'
    if (!args.port)
      port = 8080
    server = connect({ host: host, port: port }, () => {
      server.write(new Message({timestamp: getTime(), username, command: 'connect' }).toJSON() + '\n')
      callback()
    })
    server.on('data', (buffer) => {
      let msg = Message.fromJSON(buffer)
      let color = colorTable[msg.command]
      this.log(cli.chalk[color](msg.toString()))
    })
    server.on('end', () => {
      cli.exec('exit')
    })
  })
  .action(function (input, callback) {
    let inputArray = input.split(' ')
    let rest = inputArray.slice(1)
    let firstWord = inputArray[0]
    if (firstWord.charAt(0) === '@') {
      target = firstWord.slice(1)
      command = 'whisper'
      contents = rest.join(' ')
    } else if (commandList.indexOf(firstWord) > -1) {
      command = firstWord
      contents = rest.join(' ')
    } else {
      contents = inputArray.join(' ')
    }
      
    if (command === 'disconnect') {
      cli.delimiter(cli.chalk.yellow('ftd~$'))
      server.end(new Message({ timestamp: getTime(), username, command: command }).toJSON() + '\n')
    } else if (command === 'echo'){
      cli.delimiter(cli.chalk.yellow('ftd~$') + cli.chalk.green(` <${username}>`) + cli.chalk.red(' (echo):'))
      server.write(new Message({ timestamp: getTime(), username, command: command, contents }).toJSON() + '\n')      
    } else if (command === 'broadcast') {
      cli.delimiter(cli.chalk.yellow('ftd~$') + cli.chalk.green(` <${username}>`) + cli.chalk.cyan(' (all):'))
      server.write(new Message({ timestamp: getTime(), username, command: command, contents }).toJSON() + '\n')    
    } else if (command === 'users') {
      cli.delimiter(cli.chalk.yellow('ftd~$') + cli.chalk.green(` <${username}>`) + cli.chalk.magenta(` users:`))
      server.write(new Message({ timestamp: getTime(), username, command: command }).toJSON() + '\n')    
    } else if (command === 'whisper') {
      cli.delimiter(cli.chalk.yellow('ftd~$') + cli.chalk.green(` <${username}>`) + cli.chalk.yellow(` (whisper):`))
      server.write(new Message({ timestamp: getTime(), username, command: command, contents, target: target }).toJSON() + '\n')    
    } else {
      this.log(`That was an invalid command. Commands from this mode are: 'echo', 'broadcast', 'disconnect', 'users'`)
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




