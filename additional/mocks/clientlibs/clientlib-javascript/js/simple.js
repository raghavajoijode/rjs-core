"use strict";
const students = [
    {name:'Crick', id:'1', grade: 'A'},
    {name:'Rumel', id:'2', grade: 'B'},
    {name:'Sushi', id:'3', grade: 'A'},
    {name:'Kempu', id:'4', grade: 'C'}
]

const unique = (value, index, self) => self.indexOf(value) === index
const findByGrade = x => (value, index, self) => value.grade === x

console.log('filter',students.filter(value => value.grade=='A'))
console.log('map',students.map(value => value.grade))
console.log('map distinct',students.map(value => value.grade).filter(unique))
console.log('find grade \'A\'',students.filter(findByGrade('A')))