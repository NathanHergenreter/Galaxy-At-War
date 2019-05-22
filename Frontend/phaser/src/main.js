var config = 
{
    parent: 'phaser-game',
    type: Phaser.AUTO,
    width: 800,
    height: 800,
    scene: 
    {
        preload: preload,
        create: create,
        update: update,
    }
};

var GalaxyAtWar = new Phaser.Game(config);
var random = Math.floor((Math.random()*700));
var CameraMoving ;   
var KeyBoardKeys;
var WorldInfo;
var random1 = Math.floor((Math.random()*600));
var randomPlanetsAmount = 10;
var PlanetsPositionRange = { min: new Phaser.Math.Vector2(0, 0), max: new Phaser.Math.Vector2(2000, 2000) };

function preload ()
{
    this.load.image('sky','assets/Sky.jpg');
    this.load.image('planet1','assets/planet1.png');
    this.load.image('avatar','assets/avatar.png');
    this.load.image('button','assets/button.png');
}

function create ()
{
    var background = this.add.image(0, 0, 'sky').setOrigin(0);
    background.setScale(1.25);

    this.avatarPlanet =  this.add.image(0, 0, 'avatar');
    this.avatarPlanet.setScale(0.6);
    this.avatarPlanet.visible = false;
    
    this.movablePlanet =  this.add.image(0, 0, 'planet1');
    this.movablePlanet.visible = false;


    for(var i = 0; i < randomPlanetsAmount; i++)
    {
        var planet = this.add.image(Phaser.Math.Between(PlanetsPositionRange.min.x, PlanetsPositionRange.max.x), 
                                Phaser.Math.Between(PlanetsPositionRange.min.y, PlanetsPositionRange.max.y), 'planet1');
        
        planet.x += planet.width;
        planet.y += planet.height;
    }

    KeyBoardKeys = this.input.keyboard.createCursorKeys();

    var controlConfig = 
	{
		camera: this.cameras.main,
		left: KeyBoardKeys.left,
		right: KeyBoardKeys.right,
		up: KeyBoardKeys.up,
		down: KeyBoardKeys.down,
		acceleration: 0.02,
		drag: 0.0005,
		maxSpeed: 1.0
    };

    KeyBoardKeys = new Phaser.Cameras.Controls.SmoothedKeyControl(controlConfig);
    var CameraMoving = this.cameras.main;
    CameraMoving.setBounds(0, 0, 2000, 2000).setZoom(1);
    this.camera = CameraMoving;

    WorldInfo = this.add.text(10, 10).setScrollFactor(0);

    this.startButton = this.add.image(0, 0, 'button');
    this.startButton.setScale(0.5);
    this.startButton.setInteractive();
    this.startButton.setOrigin(0);
    this.startButton.setScrollFactor(0);

    this.startButton.on('pointerdown', function(pointer)
    {
        this.setTint(0xe0e0e0);

        var x = document.getElementById('xCoordinate').value;
        var y = document.getElementById('yCoordinate').value;  
        
        if(x != "" && y != "")    
        {
            x = parseInt(x);
            y = parseInt(y);

            var planet = this.scene.movablePlanet;

            if(x <= planet.width * 0.5)
                x = planet.width * 0.5;
            else if(x > PlanetsPositionRange.max.x - planet.width * 0.5)
                x = PlanetsPositionRange.max.x - planet.width * 0.5;

            if(y <= planet.height * 0.5)
                y = planet.height * 0.5;
            else if(y > PlanetsPositionRange.max.y - planet.height * 0.5)
                y = PlanetsPositionRange.max.y - planet.height * 0.5;


            setPlanetPosition(planet, this.scene.camera, x, y);
        }
    });

    this.startButton.on('pointerout', function(pointer) { this.clearTint(); });
    this.startButton.on('pointerup',  function(pointer) { this.clearTint(); });

    this.startButtonText = this.add.text(0, 0, 'Go!', { fontFamily: 'Arial', fontSize: 32, color: '#000000' });
    this.startButtonText.setOrigin(0.5);
    this.startButtonText.x = Math.floor(this.startButton.x + this.startButton.width / 2) * 0.5;
    this.startButtonText.y = Math.floor(this.startButton.y + this.startButton.height / 2) * 0.5;
    this.startButtonText.setScrollFactor(0);

    this.startButtonContainer = this.add.container(this.game.config.width * 0.005, this.game.config.height * 0.225);
    this.startButtonContainer.add(this.startButton);
    this.startButtonContainer.add(this.startButtonText);

    this.formUtil = new FormUtil
    ({
        scene: this,
        rows: 20,
        cols: 20
    });
    this.formUtil.placeElementAt(40, 'coordinates', false);
}

function update (time, delta)
{
    KeyBoardKeys.update(delta);

    WorldInfo.setText
    ([
        'screen x: ' + this.input.x,
        'screen y: ' + this.input.y,
        'world x: ' + this.input.mousePointer.worldX,
        'world y: ' + this.input.mousePointer.worldY
    ]);

    this.avatarPlanet.visible = this.movablePlanet.visible;
    this.avatarPlanet.x = this.movablePlanet.x;
    this.avatarPlanet.y = this.movablePlanet.y;
}


function setPlanetPosition(planet, camera, x, y)
{
    camera.centerOn(x, y);

    planet.x = x;
    planet.y = y;

    planet.visible = true;
}