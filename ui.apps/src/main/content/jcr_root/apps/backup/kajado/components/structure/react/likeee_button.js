'use strict';

const e = React.createElement;

class LikeButton extends React.Component {
  constructor(props) {
    super(props);
    this.state = { liked: false };
  }

  render() {
    if (this.state.liked) {
      return 'You liked this.';
    }

   /* return e(
      'button',
      { onClick: () => this.setState({ liked: true }) },
      'Like'
    );*/
    return (`<button onClick={() => this.setState({ liked: true }) }>
    	        Like
    	      </button>`);
  }
}


const domContainer = document.querySelector('#like_button_container');
//ReactDOM.render(e(LikeButton), domContainer);
ReactDOM.render(<LikeButton />, domContainer);


/*Step 1: Run npm init -y (if it fails, here’s a fix)
Step 2: Run npm install babel-cli@6 babel-preset-react-app@3

npx babel --watch source --out-dir . --presets react-app/prod */