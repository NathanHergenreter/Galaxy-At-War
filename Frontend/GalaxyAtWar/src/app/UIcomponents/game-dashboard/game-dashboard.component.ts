import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { PlatformLocation } from '@angular/common'
import 'phaser-ce/build/custom/pixi';
import 'phaser-ce/build/custom/p2';
import Phaser from 'phaser-ce/build/custom/phaser-split';
import { FormUtil } from '../../../assets/js/formUtil';
import { AlignGrid } from '../../../assets/js/alignGrid';
import { Router, ActivatedRoute } from '@angular/router';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { Message } from 'src/app/interfaces/Message';
import $ from 'jquery';
import jQuery from 'jquery';
import { Helper } from './helper';
import { UserSessionService } from 'src/app/services/user-session.service';
import { XmlhttpService } from 'src/app/services/xmlhttp.service';
import { GameUpdaterService } from 'src/app/services/game-updater.service';



@Component({
  selector: 'app-game-dashboard',
  templateUrl: './game-dashboard.component.html',
  styleUrls: ['./game-dashboard.component.css']
})
export class GameDashboardComponent implements OnInit {

  @ViewChild('xCoordinate') xCoordinate: ElementRef;
  @ViewChild('yCoordinate') yCoordinate: ElementRef;

  KeyBoardKeys;
  Player: any;
  Money: any;
  Point: any;
  ArmyAmount: any = 0;
  GameId: any;
  Color;

  phaserGame: Phaser.Game;
  carrot: any;
  preloadStatus: string = "Preloading";
  config: any;
  gameScene: Phaser.Class;
  _username: string;

  constructor(
    private _router: Router,
    private _route: ActivatedRoute,
    location: PlatformLocation,
    private _userSession: UserSessionService,
    private _xmlhttp: XmlhttpService,
    private _gameUpdater: GameUpdaterService) {

    let initSetup: any = this._route.snapshot.params;

    this.Player = initSetup.playerCount;
    this.Money = initSetup.startingMoney;
    this.Point = initSetup.startPoint;
    this.GameId = parseInt(initSetup.gameId, 10);
    this.Color = initSetup.playerColor;
    localStorage.setItem('money', this.Money);
    localStorage.setItem('player', this.Player);
    localStorage.setItem('point', this.Point);
    localStorage.setItem('ArmayAmount', "0");
    localStorage.setItem('FleetAmount', "0");

    let theScene = this.getScene(initSetup, _gameUpdater);

    theScene.setPlanetPosition = this.setPlanetPosition;
    theScene.setLabelAmount = this.setLabelAmount;
    theScene.onAddUnitsAttempt = this.onAddUnitsAttempt;
    theScene.refreshArmyUnits = this.refreshArmyUnits;
    theScene.refreshFleetUnits = this.refreshFleetUnits;

    _gameUpdater._teamUpdater.NewTeamMemberAdded.on(theScene.test);

    this.gameScene = new Phaser.Class(theScene);
    this.config = {
      type: Phaser.AUTO,
      width: 800,
      height: 800,
      parent: 'phaser-example',
      scale:
      {
        mode: Phaser.Scale.FIT,
        autoCenter: Phaser.Scale.CENTER_BOTH,
      },
      scene: this.gameScene
    };
    this.phaserGame = new Phaser.Game(this.config);
    this._username = _userSession.getUsername();
  }

  getScene(gameConfig, server:GameUpdaterService): any {
    return {
      Extends: Phaser.Scene,
      initialize: function GameScene(config) {
        Phaser.Scene.call(this, config);
      },
      _gameConfig: gameConfig,
      _server: server,
      onPlayerMoved: function (data) {
        //TODO
      },
      preload: function () {
        this.load.image('sky', 'assets/Sky.jpg');
        this.load.image('planet1', 'assets/planet1.png');
        this.load.image('avatar', 'assets/avatar.png');
        this.load.image('button', 'assets/button.png');

        this.load.image('quad', 'assets/quad.png');
        this.load.image('money_icon', 'assets/money_icon.png');
        this.load.image('points_icon', 'assets/points_icon.png');
        this.load.image('clock_icon', 'assets/clock_icon.png');
        this.load.image('manpower_icon', 'assets/manpower_icon.png');
        this.load.image('alloy_icon', 'assets/alloy_icon.png');

        this.load.image('clock_bar', 'assets/clock_bar.png');
        this.load.image('left_bar', 'assets/left_bar.png');

        this.load.image('rect', 'assets/rect.png');
        this.load.image('rect_bordered', 'assets/rect_bordered.png');
        this.load.image('pent', 'assets/pent.png');
        this.load.image('pent_bordered', 'assets/pent_bordered.png');
        this.load.image('rect_button', 'assets/rect_button.png');

        this.load.image('assault_cruiser', 'assets/icons/ships/assault_cruiser.png');
        this.load.image('battlecruiser', 'assets/icons/ships/battlecruiser.png');
        this.load.image('carrier', 'assets/icons/ships/carrier.png');
        this.load.image('frigate', 'assets/icons/ships/frigate.png');
        this.load.image('heavy_cruiser', 'assets/icons/ships/heavy_cruiser.png');
        this.load.image('heavy_transport', 'assets/icons/ships/heavy_transport.png');
        this.load.image('light_cruiser', 'assets/icons/ships/light_cruiser.png');
        this.load.image('light_transport', 'assets/icons/ships/light_transport.png');
        this.load.image('super_carrier', 'assets/icons/ships/super_carrier.png');

        this.load.image('air_assault_corps', 'assets/icons/units/air_assault_corps.png');
        this.load.image('android_corps', 'assets/icons/units/android_corps.png');
        this.load.image('armor_corps', 'assets/icons/units/armor_corps.png');
        this.load.image('drone_corps', 'assets/icons/units/drone_corps.png');
        this.load.image('fleet_marine_force', 'assets/icons/units/fleet_marine_force.png');
        this.load.image('infantry_corps', 'assets/icons/units/infantry_corps.png');
        this.load.image('marine_brigade', 'assets/icons/units/marine_brigade.png');
        this.load.image('marine_corps', 'assets/icons/units/marine_corps.png');
        this.load.image('marine_division', 'assets/icons/units/marine_division.png');
        this.load.image('mech_corps', 'assets/icons/units/mech_corps.png');
        this.load.image('militia_corps', 'assets/icons/units/militia_corps.png');
      },
      create: function () {
        let randomPlanetsAmount = parseInt(this._gameConfig.planetCount);
        let PlanetsPositionRange = { min: new Phaser.Math.Vector2(0, 0), max: new Phaser.Math.Vector2(2000, 2000) };
        let PlanetLabel = 'Planet1';
        let ArmyAmount = 0;
        let FleetAmount = 0;
        var background = this.add.image(0, 0, 'sky').setOrigin(0);
        background.setScale(1.25);

        this.dirLine1 = this.add.line(0, 0, 0, 0, 0, 0, 0x6666ff);
        this.dirLine2 = this.add.line(0, 0, 0, 0, 0, 0, 0x6666ff);
        this.dirLine1.visible = false;
        this.dirLine2.visible = false;

        this.avatarPlanet = this.add.image(0, 0, 'avatar');
        this.avatarPlanet.setScale(0.6);
        this.avatarPlanet.visible = false;

        this.movablePlanet = this.add.image(Phaser.Math.Between(PlanetsPositionRange.min.x, PlanetsPositionRange.max.x),
          Phaser.Math.Between(PlanetsPositionRange.min.y, PlanetsPositionRange.max.y), 'planet1');
        this.movablePlanet.visible = false;
        this.movablePlanet.setInteractive();
        this.canClearLines = false;

        this.movablePlanet.on('pointerdown', function (pointer) {
          this.scene.movablePlanetArc.visible = true;
          this.scene.dirLine1.visible = true;
          this.scene.dirLine2.visible = true;

          this.scene.canClearLines = false;
        });
        this.movablePlanet.on('pointerout', function (pointer) {
          this.scene.canClearLines = true;
        });

        this.input.on('pointerdown', function (pointer) {
          if (this.canClearLines) {
            this.movablePlanetArc.visible = false;
            this.dirLine1.visible = false;
            this.dirLine2.visible = false;
          }

          if (!this.inputCoordinateWasSelected) {
            if (this.selectedInputCoordinate != null)
              this.selectedInputCoordinate.label.setTint(0xc0c0c0);

            this.selectedInputCoordinate = null;
          }

          this.inputCoordinateWasSelected = false;

        }, this);

        this.movablePlanetArc = this.add.arc(0, 0, 60, 0, 360, false);
        this.movablePlanetArc.setStrokeStyle(2, 0x6666ff);
        this.movablePlanetArc.visible = false;

        this.movablePlanets = [];
        this.movablePlanets.push(this.movablePlanet);

        this.dirLines = [this.dirLine1, this.dirLine2];

        this.movablePlanetLabel = this.add.text(0, 0, PlanetLabel, { fontFamily: 'Arial', fontSize: 20, color: '#ffffff' });
        this.movablePlanetLabel.setOrigin(0.5);

        this.canMovePlanet = false;

        this.planets = [];

        for (var i = 0; i < randomPlanetsAmount; i++) {
          var planet = this.add.image(Phaser.Math.Between(PlanetsPositionRange.min.x, PlanetsPositionRange.max.x),
            Phaser.Math.Between(PlanetsPositionRange.min.y, PlanetsPositionRange.max.y), 'planet1');

          planet.x += planet.width;
          planet.y += planet.height;

          planet.setInteractive();
          planet.on('pointerdown', function (pointer) {
            if (!this.addedToList) {
              //this.scene.moveToPlanetButtonContainer.visible = true;
              this.scene.selectedPlanet = this;

              if (this.scene.canMovePlanet) {
                this.setTint(0xe0e0e0);

                if (this.scene.selectedPlanet != undefined) {
                  if (!this.scene.selectedPlanet.addedToList) {
                    this.scene.selectedPlanet.avatar.visible = true;

                    this.scene.selectedPlanet.created = true;
                    this.scene.movablePlanets.push(this.scene.selectedPlanet);
                    this.scene.createPlanetsLabel();

                    this.scene.selectedPlanet.addedToList = true;
                  }
                }

                //this.scene.moveToPlanetButtonContainer.visible = false;
                this.scene.canMovePlanet = false;
              }
            }
          });

          planet.avatar = this.add.image(planet.x, planet.y, 'avatar');
          planet.avatar.setScale(0.6);
          planet.avatar.visible = false;
          planet.addedToList = false;

          this.planets.push(planet);
        }

        this.KeyBoardKeys = this.input.keyboard.createCursorKeys();

        var controlConfig =
          {
            camera: this.cameras.main,
            left: this.KeyBoardKeys.left,
            right: this.KeyBoardKeys.right,
            up: this.KeyBoardKeys.up,
            down: this.KeyBoardKeys.down,
            acceleration: 0.02,
            drag: 0.0005,
            maxSpeed: 1.0
          };

        this.KeyBoardKeys = new Phaser.Cameras.Controls.SmoothedKeyControl(controlConfig);
        var CameraMoving = this.cameras.main;
        CameraMoving.setBounds(0, 0, 2000, 2000).setZoom(1);
        this.camera = CameraMoving;

        function makeLabelWithIcon(title, iconName, xPos, yPos, originX, originY) {
          var label = this.add.text(xPos, yPos, title, { fontFamily: 'Arial', fontSize: 18, color: '#ffffff' });;
          label.setOrigin(originX, originY);
          label.setScrollFactor(0);

          var icon = this.add.image(label.x - this.game.config.width * 0.060, label.y, iconName);
          icon.setScrollFactor(0);
          icon.setScale(0.4);
          icon.setOrigin(originX, originY);

          return { label: label, icon: icon };
        }

        this.moneyLabel = makeLabelWithIcon.call(this, 'Money', 'money_icon', this.game.config.width * 0.085, this.game.config.height * 0.03, 0, 0.5);
        this.moneyLabel.amount = 0;
        this.setLabelAmount(this.moneyLabel, 600);

        this.manPower = makeLabelWithIcon.call(this, '', 'manpower_icon', (this.game.config.width * 0.085) + (this.moneyLabel.label.width + this.moneyLabel.icon.width)
          , this.game.config.height * 0.03, 0, 0.5);
        this.manPower.amount = 0;
        this.setLabelAmount(this.manPower, 0);

        this.alloys = makeLabelWithIcon.call(this, '', 'alloy_icon', (this.game.config.width * 0.085) + (this.moneyLabel.label.width + this.moneyLabel.icon.width) * 2
          , this.game.config.height * 0.03, 0, 0.5);
        this.alloys.amount = 0;
        this.setLabelAmount(this.alloys, 0);

        this.leftBar = this.add.image(this.game.config.width * -0.15, this.moneyLabel.label.y - this.moneyLabel.label.y * 0.55, 'left_bar');
        this.leftBar.setScale(0.8);
        this.leftBar.setOrigin(0.15, 0.5);
        this.leftBar.setScrollFactor(0);

        this.clockLabel = this.add.text(this.game.config.width * 0.94, 0, "0", { fontFamily: 'Arial', fontSize: 18, color: '#ffffff' });
        this.clockLabel.setOrigin(1, 0.5);
        this.clockLabel.setScrollFactor(0);
        this.clockLabel.y += this.clockLabel.height * 1.1;

        this.clockIcon = this.add.image(this.game.config.width * 0.98, this.clockLabel.y, 'clock_icon');
        this.clockIcon.setScale(0.25);
        this.clockIcon.setOrigin(1, 0.5);
        this.clockIcon.setScrollFactor(0);

        this.clockBar = this.add.image(this.game.config.width * 1.08, this.clockLabel.y - this.clockLabel.y * 0.5, 'clock_bar');
        this.clockBar.setScale(0.8);
        this.clockBar.setOrigin(0.5, 0.5);
        this.clockBar.setScrollFactor(0);

        function makePanel(posX, posY, panelTitle, contentHeight, panelOriginX = 1, panelTitleOriginX = 0.5, onPointerDown = null) {
          var panelContainer = this.add.container(0, 0);

          var panel = this.add.image(posX, posY, 'quad');
          panel.setScale(7, contentHeight);
          panel.setOrigin(panelOriginX, 1);
          panel.setScrollFactor(0);
          panel.setTint(0xbfbfbf);

          var panelLabelBackground = this.add.image(posX, posY - panel.height * panel.scaleY, 'quad');
          panelLabelBackground.setScale(7, 1.2);
          panelLabelBackground.setOrigin(panelOriginX, 1);
          panelLabelBackground.setScrollFactor(0);
          panelLabelBackground.setTint(0x000000);
          panelLabelBackground.setInteractive();
          panelLabelBackground.panel = panelContainer;
          panelLabelBackground.on('pointerdown', function (pointer) {
            this.setTint(0x161616);
            var tween = this.scene.tweens.add
              ({
                targets: this.panel,
                y: this.panel.hide ? 0 : this.panel.hidePositionY,
                ease: 'Power1',
                duration: 400,
                repeat: 0,
              });

            this.panel.hide = !this.panel.hide;

            if (onPointerDown != null) {
              onPointerDown.call(this, pointer);
            }
          });
          panelLabelBackground.on('pointerup', function (pointer) {
            this.setTint(0x000000);
          });
          panelLabelBackground.on('pointerout', function (pointer) {
            this.setTint(0x000000);
          });

          var panelLabel = this.add.text(panelLabelBackground.x - panelLabelBackground.width * panelLabelBackground.scaleX * 0.5,
            panelLabelBackground.y,
            panelTitle, { fontFamily: 'Arial', fontSize: 24, color: '#ffffff' });

          panelLabel.setOrigin(panelTitleOriginX, 1.1);
          panelLabel.setScrollFactor(0);

          panelContainer.add(panel);
          panelContainer.add(panelLabelBackground);
          panelContainer.add(panelLabel);

          panelContainer.panel = panel;
          panelContainer.panelLabelBackground = panelLabelBackground;
          panelContainer.panelLabel = panelLabel;

          panelContainer.y += panel.height * panel.scaleY;
          panelContainer.hide = true;
          panelContainer.hidePositionY = panelContainer.y;

          panelContainer.nextItemOffsetY = 0;
          panelContainer.contentHeight = contentHeight;

          return panelContainer;
        }

        this.addItemToPanel = function (panelContainer, title, originX, originY, onPointerDown) {
          var label = this.add.text(panelContainer.panel.x - panelContainer.panel.width * panelContainer.panel.scaleX,
            panelContainer.panel.y - panelContainer.panel.height * panelContainer.panel.scaleY * 0.98,
            title, { fontFamily: 'Arial', fontSize: 20, color: '#000000' });

          label.x += label.width * 0.1
          label.y += label.height * 1.7;
          label.y += panelContainer.nextItemOffsetY;

          label.setOrigin(originX, originY);
          label.setScrollFactor(0);

          label.setInteractive();
          label.on('pointerdown', onPointerDown);
          label.on('pointerup', function (pointer) { this.setColor("#000000"); });
          label.on('pointerout', function (pointer) { this.setColor("#000000"); });

          panelContainer.add(label);

          panelContainer.nextItemOffsetY += label.height;

          return label;
        }

        function addButtonItemToPanel(panelContainer, title, originX, originY, onPointerDown) {
          var label = this.add.text(panelContainer.panel.x,
            panelContainer.panel.y - panelContainer.panel.height * panelContainer.panel.scaleY * 0.98,
            title, { fontFamily: 'Arial', fontSize: 20, color: '#000000' });

          label.x += label.width * 0.1;
          label.y += label.height * 1.7;
          label.y += panelContainer.nextItemOffsetY;

          label.setOrigin(originX, originY);
          label.setScrollFactor(0);

          label.setInteractive();

          var rect = this.add.image(panelContainer.panel.x + panelContainer.panel.width * panelContainer.panel.scaleX, label.y, 'rect_button');
          rect.setOrigin(1.5, 1);
          rect.setScrollFactor(0);
          rect.setInteractive();
          rect.on('pointerdown', onPointerDown);
          rect.on('pointerup', function (pointer) { this.setTint(0xffffff); });
          rect.on('pointerout', function (pointer) { this.setTint(0xffffff); });

          rect.label = label;

          var rectLabel = this.add.text(rect.x, rect.y, '+', { fontFamily: 'Arial', fontSize: 20, color: '#000000' });
          rectLabel.setOrigin(0.5, 1);
          rectLabel.setScrollFactor(0);
          rectLabel.x -= rect.width;
          rectLabel.y -= rect.height * 0.1;

          panelContainer.add(label);
          panelContainer.add(rect);
          panelContainer.add(rectLabel);

          panelContainer.nextItemOffsetY += label.height;

          return label;
        }

        this.createPlanetsLabel = function () {
          if (this.planetsPanel != undefined) {
            this.planetsPanel.destroy();
          }

          this.planetsPanel = makePanel.call(this, this.game.config.width, this.game.config.height, 'Planets', this.movablePlanets.length * 1.1);
          for (var i = 0; i < this.movablePlanets.length; i++) {
            var movablePlanet = this.movablePlanets[i];

            this.planetLabel = this.addItemToPanel.call(this, this.planetsPanel, 'Planet ' + (i + 1), 0, 1.4, function (pointer) {
              this.setColor("#4a4a4a");
              if (this.planet.created != undefined) {
                this.scene.setPlanetPosition(this.planet, this.scene.planets, this.scene.dirLines, this.scene.camera);
              }
            });

            this.planetLabel.planet = movablePlanet;
          }
        }
        this.createPlanetsLabel();

        this.activeUnit = 'Army';
        this.unitsPanel = makePanel.call(this, 0, this.game.config.height, 'Army  |  Fleet', 1.8, 0, -1, function (pointer) {
          if (!this.panel.hide) {
            this.scene.resForArmyContainer.visible = false;
            this.scene.resForFleetContainer.visible = false;
            this.scene.popupErrorMsg.visible = false;

            var middle = this.panel.panel.x + this.panel.panel.width * this.panel.panel.scaleX * 0.45;
            if (pointer.x < middle) {
              this.scene.popupDescription.text = 'Army units are used to invade other planets';
              this.scene.refreshArmyUnits(this.scene);
            }
            else {
              this.scene.popupDescription.text = 'Fleet units are used to reach other planets';
              this.scene.refreshFleetUnits(this.scene);
            }

            this.scene.popupTitle.text = this.scene.activeUnit;
            this.scene.popupDescription.text += "\n" + "To build an " + this.scene.activeUnit + " units you need:";
          }
        });

        this.armyUnitLabel = addButtonItemToPanel.call(this, this.unitsPanel, 'Army Unit: 0', 0, 1.2, function (pointer) {
          this.setTint(0x4a4a4a);
          this.scene.popupWindow.visible = true;
        });

        this.planetArmyUnitBorder2 = this.add.image(300, 300, 'rect_bordered');
        this.planetArmyUnitBorder2.setOrigin(0.5);

        this.planetArmyUnitBorder = this.add.image(300, 300, 'rect_bordered');
        this.planetArmyUnitBorder.setOrigin(0.5);
        this.planetArmyUnitBorder.setInteractive();
        this.planetArmyUnitBorder.on('pointerdown', function (pointer) {
          var mouseButton = pointer.event.which;
          if (mouseButton == 1) {
            this.planetArmyUnitBorder.setTint(0x4a4a4a);
            this.canMovePlanet = true;
          }
          else if (mouseButton == 3) {
            this.disembarkButtonContainer.visible = true;
          }
        }, this);

        this.planetArmyUnitBorder.on('pointerup', function (pointer) {
          this.clearTint();
        });
        this.planetArmyUnitBorder.on('pointerout', function (pointer) {
          this.clearTint();
        });

        this.planetArmyUnitLabel = this.add.text(300, 300, '0', { fontFamily: 'Arial', fontSize: 18, color: '#000000' });
        this.planetArmyUnitLabel.visible = true;
        this.planetArmyUnitLabel.setOrigin(0.5);

        this.planetFleetUnitBorder2 = this.add.image(300, 300, 'pent_bordered');
        this.planetFleetUnitBorder2.setOrigin(0.5);

        this.planetFleetUnitBorder = this.add.image(300, 300, 'pent_bordered');
        this.planetFleetUnitBorder.setOrigin(0.5);

        this.planetFleetUnitLabel = this.add.text(300, 300, '0', { fontFamily: 'Arial', fontSize: 18, color: '#000000' });
        this.planetFleetUnitLabel.visible = true;
        this.planetFleetUnitLabel.setOrigin(0.5);

        this.disembarkButton = this.add.image(0, 0, 'button');
        this.disembarkButton.setScale(0.4);
        this.disembarkButton.setInteractive();
        this.disembarkButton.setOrigin(0.5);
        this.disembarkButton.on('pointerdown', function (pointer) {
          this.setTint(0xe0e0e0);

        });
        this.disembarkButton.on('pointerout', function (pointer) { this.clearTint(); });
        this.disembarkButton.on('pointerup', function (pointer) { this.clearTint(); });

        this.disembarkButtonText = this.add.text(0, 0, 'Disembark', { fontFamily: 'Arial', fontSize: 18, color: '#000000' });
        this.disembarkButtonText.setOrigin(0.5);
        this.disembarkButtonContainer = this.add.container(this.game.config.width * 0.005, this.game.config.height * 0.225);
        this.disembarkButtonContainer.add(this.disembarkButton);
        this.disembarkButtonContainer.add(this.disembarkButtonText);

        this.disembarkButtonContainer.visible = false;

        this.startButton = this.add.image(0, 0, 'button');
        this.startButton.setScale(0.5);
        this.startButton.setInteractive();
        this.startButton.setOrigin(0);
        this.startButton.setScrollFactor(0);

        this.startButton.on('pointerdown', function (pointer) {
          this.setTint(0xe0e0e0);

          var x = this.scene.inputTextX.text;
          var y = this.scene.inputTextY.text;

          if (x != "" && y != "") {
            x = parseInt(x);
            y = parseInt(y);

            var planet = this.scene.movablePlanet;

            if (x <= planet.width * 0.5)
              x = planet.width * 0.5;
            else if (x > PlanetsPositionRange.max.x - planet.width * 0.5)
              x = PlanetsPositionRange.max.x - planet.width * 0.5;

            if (y <= planet.height * 0.5)
              y = planet.height * 0.5;
            else if (y > PlanetsPositionRange.max.y - planet.height * 0.5)
              y = PlanetsPositionRange.max.y - planet.height * 0.5;

            planet.x = x;
            planet.y = y;

            this.scene.setPlanetPosition(planet, this.scene.planets, this.scene.dirLines, this.scene.camera, x, y);
          }
        });

        this.startButton.on('pointerout', function (pointer) { this.clearTint(); });
        this.startButton.on('pointerup', function (pointer) {
          this.clearTint();

          this.visible = false;

          this.scene.coordinateLabelsContainer.visible = false;
          this.scene.startButtonText.visible = false;
        });

        this.startButtonText = this.add.text(0, 0, 'Go!', { fontFamily: 'Arial', fontSize: 32, color: '#000000' });
        this.startButtonText.setOrigin(0.5);
        this.startButtonText.x = Math.floor(this.startButton.x + this.startButton.width / 2) * 0.5;
        this.startButtonText.y = Math.floor(this.startButton.y + this.startButton.height / 2) * 0.5;
        this.startButtonText.setScrollFactor(0);

        this.startButtonContainer = this.add.container(this.game.config.width * 0.005, this.game.config.height * 0.225);
        this.startButtonContainer.add(this.startButton);
        this.startButtonContainer.add(this.startButtonText);

        // Starting popup window

        this.popupWindow = this.add.container(this.game.config.width * 0.5, this.game.config.height * 0.5);
        this.popupBackground = this.add.image(0, 0, 'quad');
        this.popupBackground.setOrigin(0.5);
        this.popupBackground.setScale(this.game.config.width * 0.025, this.game.config.height * 0.015);
        this.popupBackground.setTint(0x4472c4);

        this.popupClose = this.add.image(0, 0, 'quad');
        this.popupClose.setInteractive();
        this.popupClose.setScrollFactor(0);
        this.popupClose.setScale(1.2);
        this.popupClose.x += (this.popupBackground.width * this.popupBackground.scaleX * 0.5) - (this.popupClose.width * this.popupClose.scaleX * 0.6);
        this.popupClose.y -= (this.popupBackground.height * this.popupBackground.scaleY * 0.5) - (this.popupClose.height * this.popupClose.scaleY * 0.6);
        this.popupClose.on('pointerdown', function (pointer) {
          this.scene.popupWindow.visible = false;
        });
        this.popupCloseText = this.add.text(this.popupClose.x, this.popupClose.y, "X", { fontFamily: 'Arial', fontSize: 18, color: '#000000' });
        this.popupCloseText.setOrigin(0.5);

        this.popupTitleBar = this.add.image(0, this.popupClose.y, 'quad');
        this.popupTitleBar.setOrigin(0.5);
        this.popupTitleBar.setScale(5.5, 1.3);

        this.popupTitle = this.add.text(0, this.popupClose.y, "Army", { fontFamily: 'Arial', fontSize: 22, color: '#000000' });
        this.popupTitle.setOrigin(0.5);
        this.popupTitle.y += this.popupTitle.height * 0.2;
        this.popupTitleBar.y = this.popupTitle.y;

        this.popupDescription = this.add.text(0, -(this.popupBackground.height * this.popupBackground.scaleY * 0.12), "", { fontFamily: 'Arial', fontSize: 22, color: '#ffffff' });
        this.popupDescription.setOrigin(0.5);
        this.popupDescription.visible = false;

        this.popupErrorMsg = this.add.text(0, 0, "Not enough resources", { fontFamily: 'Arial', fontSize: 22, color: '#ffffff' });
        this.popupErrorMsg.setOrigin(0.5);
        this.popupErrorMsg.setTint(0xaf1d1d);
        this.popupErrorMsg.y += (this.popupBackground.height * this.popupBackground.scaleY * 0.4);
        this.popupErrorMsg.visible = false;

        // this.popupAddButton = this.add.image(0, 0, 'quad');
        // this.popupAddButton.setInteractive();
        // this.popupAddButton.setScrollFactor(0);
        // this.popupAddButton.setScale(1.2);
        // this.popupAddButton.x += (this.popupBackground.width * this.popupBackground.scaleX * 0.5) - (this.popupAddButton.width * this.popupAddButton.scaleX * 0.6);
        // this.popupAddButton.y += (this.popupBackground.height * this.popupBackground.scaleY * 0.5) - (this.popupAddButton.height * this.popupAddButton.scaleY * 0.6);
        // this.popupAddButton.on('pointerdown', function(pointer)
        // {
        //     onAddUnitsAttempt.call(this.scene);
        // });
        // this.popupAddButtonText = this.add.text(this.popupAddButton.x, this.popupAddButton.y, "+", { fontFamily: 'Arial', fontSize: 18, color: '#000000' });
        // this.popupAddButtonText.setOrigin(0.5);

        // this.unitsIcon = this.add.image(0, 0, 'rect');
        // this.unitsIcon.setOrigin(0.5);
        // this.unitsIcon.setScale(1.2);
        // this.unitsIcon.x += (this.popupBackground.width * this.popupBackground.scaleX * 0.5) - (this.unitsIcon.width * this.unitsIcon.scaleX * 1.5);
        // this.unitsIcon.y += (this.popupBackground.height * this.popupBackground.scaleY * 0.5) - (this.unitsIcon.height * this.unitsIcon.scaleY * 0.6);
        // this.unitsIconText = this.add.text(this.unitsIcon.x, this.unitsIcon.y, "0", { fontFamily: 'Arial', fontSize: 18, color: '#ffffff' });
        // this.unitsIconText.setOrigin(0.5);

        var consumableResourceLineHeight = -(this.popupBackground.height * this.popupBackground.scaleY * 0.4);
        var consumableResTitleHor = undefined;

        this.addConsumableResourceLine = function (iconName, title, manPowerRequired, alloyRequired, x) {
          var consumableContainer = this.add.container(x, consumableResourceLineHeight);

          var icon = this.add.image(0, 0, iconName);
          icon.setOrigin(0, 0.5);
          icon.setScale(1.35);

          var label = this.add.text(0, 0, title, { fontFamily: 'Arial', fontSize: 22, color: '#ffffff' })
          label.setOrigin(0, 0.5);

          if (consumableResTitleHor == undefined)
            consumableResTitleHor = label.width;

          label.x += consumableResTitleHor * 0.5;

          var manpowerIcon = this.add.image(label.x + consumableResTitleHor * 1.5, 0, 'manpower_icon');
          manpowerIcon.setOrigin(0.5);
          manpowerIcon.setScale(0.4);

          var manpowerLabel = this.add.text(manpowerIcon.x, 0, '' + manPowerRequired, { fontFamily: 'Arial', fontSize: 22, color: '#ffffff' })
          manpowerLabel.setOrigin(0, 0.5);
          manpowerLabel.x += manpowerIcon.width * 0.3;


          var alloyIcon = this.add.image(manpowerIcon.x + manpowerIcon.width, 0, 'alloy_icon');
          alloyIcon.setOrigin(0.5);
          alloyIcon.setScale(0.4);

          var alloyLabel = this.add.text(alloyIcon.x, 0, '' + alloyRequired, { fontFamily: 'Arial', fontSize: 22, color: '#ffffff' })
          alloyLabel.setOrigin(0, 0.5);
          alloyLabel.x += alloyIcon.width * 0.3;

          var popupAddButton = this.add.image(alloyLabel.x + alloyIcon.width * 1.6, 0, 'quad');
          popupAddButton.setInteractive();
          popupAddButton.setScrollFactor(0);
          popupAddButton.setScale(1);
          popupAddButton.manPowerRequired = manPowerRequired;
          popupAddButton.alloyRequired = alloyRequired;
          popupAddButton.on('pointerdown', function (pointer) {
            this.setTint(0xe0e0e0);
            this.scene.onAddUnitsAttempt(this.scene, 0, this.manPowerRequired, this.alloyRequired);
          });
          popupAddButton.on('pointerup', function (pointer) { this.clearTint(); });
          popupAddButton.on('pointerout', function (pointer) { this.clearTint(); });

          var popupAddButtonText = this.add.text(popupAddButton.x, popupAddButton.y, "+", { fontFamily: 'Arial', fontSize: 18, color: '#000000' });
          popupAddButtonText.setOrigin(0.5);

          consumableContainer.add(icon);
          consumableContainer.add(label);
          consumableContainer.add(manpowerIcon);
          consumableContainer.add(manpowerLabel);
          consumableContainer.add(alloyIcon);
          consumableContainer.add(alloyLabel);

          consumableContainer.add(popupAddButton);
          consumableContainer.add(popupAddButtonText);

          consumableResourceLineHeight += icon.height * icon.scaleY;

          return consumableContainer;
        };

        // Resources for army container
        this.resForArmyContainer = this.add.container(0, (this.popupBackground.height * this.popupBackground.scaleY * 0.1));
        // this.moneyIcon = this.add.image(-(this.popupBackground.width * this.popupBackground.scaleX * 0.2), 0, 'money_icon');
        // this.moneyIcon.setOrigin(0.5);
        // this.moneyIcon.setScale(0.6);
        // this.moneyIconText = this.add.text(this.moneyIcon.x, this.moneyIcon.y, "500", { fontFamily: 'Arial', fontSize: 20, color: '#ffffff' });
        // this.moneyIconText.setOrigin(0.5);
        // this.moneyIconText.y += this.moneyIconText.height * 2;

        // this.manpowerIcon = this.add.image((this.popupBackground.width * this.popupBackground.scaleX * 0.2), 0, 'manpower_icon');
        // this.manpowerIcon.setOrigin(0.5);
        // this.manpowerIcon.setScale(0.6);
        // this.manpowerIconText = this.add.text(this.manpowerIcon.x, this.manpowerIcon.y, "10", { fontFamily: 'Arial', fontSize: 20, color: '#ffffff' });
        // this.manpowerIconText.setOrigin(0.5);
        // this.manpowerIconText.y += this.manpowerIconText.height * 2;

        // this.resForArmyContainer.add(this.moneyIcon);
        // this.resForArmyContainer.add(this.moneyIconText);
        // this.resForArmyContainer.add(this.manpowerIcon);
        // this.resForArmyContainer.add(this.manpowerIconText);
        this.resForArmyContainer.visible = false;

        var widthLOR = -this.popupBackground.width * this.popupBackground.scaleX * 0.43;
        var armyLOR = [];
        armyLOR.push(this.addConsumableResourceLine('infantry_corps', 'Infantry Corps', 12, 5, widthLOR));
        armyLOR.push(this.addConsumableResourceLine('drone_corps', 'Drone Corps', 0, 12, widthLOR));
        armyLOR.push(this.addConsumableResourceLine('armor_corps', 'Armor Corps', 10, 12, widthLOR));
        armyLOR.push(this.addConsumableResourceLine('android_corps', 'Android Corps', 0, 8, widthLOR));
        armyLOR.push(this.addConsumableResourceLine('air_assault_corps', 'Air Assault', 14, 6, widthLOR));
        armyLOR.push(this.addConsumableResourceLine('militia_corps', 'Militia Corps', 10, 0, widthLOR));
        armyLOR.push(this.addConsumableResourceLine('mech_corps', 'Mech Corps', 12, 10, widthLOR));

        for (var i = 0; i < armyLOR.length; i++) this.resForArmyContainer.add(armyLOR[i]);

        // Resources for fleet container
        this.resForFleetContainer = this.add.container(0, (this.popupBackground.height * this.popupBackground.scaleY * 0.1));
        // this.moneyIcon = this.add.image(-(this.popupBackground.width * this.popupBackground.scaleX * 0.25), 0, 'money_icon');
        // this.moneyIcon.setOrigin(0.5);
        // this.moneyIcon.setScale(0.6);
        // this.moneyIconText = this.add.text(this.moneyIcon.x, this.moneyIcon.y, "500", { fontFamily: 'Arial', fontSize: 20, color: '#ffffff' });
        // this.moneyIconText.setOrigin(0.5);
        // this.moneyIconText.y += this.moneyIconText.height * 2;

        // this.alloyIcon = this.add.image(0, 0, 'alloy_icon');
        // this.alloyIcon.setOrigin(0.5);
        // this.alloyIcon.setScale(0.6);
        // this.alloyIconText = this.add.text(this.alloyIcon.x, this.alloyIcon.y, "5", { fontFamily: 'Arial', fontSize: 20, color: '#ffffff' });
        // this.alloyIconText.setOrigin(0.5);
        // this.alloyIconText.y += this.alloyIconText.height * 2;

        // this.manpowerIcon = this.add.image((this.popupBackground.width * this.popupBackground.scaleX * 0.25), 0, 'manpower_icon');
        // this.manpowerIcon.setOrigin(0.5);
        // this.manpowerIcon.setScale(0.6);
        // this.manpowerIconText = this.add.text(this.manpowerIcon.x, this.manpowerIcon.y, "10", { fontFamily: 'Arial', fontSize: 20, color: '#ffffff' });
        // this.manpowerIconText.setOrigin(0.5);
        // this.manpowerIconText.y += this.manpowerIconText.height * 2;

        // this.resForFleetContainer.add(this.moneyIcon);
        // this.resForFleetContainer.add(this.moneyIconText);
        // this.resForFleetContainer.add(this.alloyIcon);
        // this.resForFleetContainer.add(this.alloyIconText);
        // this.resForFleetContainer.add(this.manpowerIcon);
        // this.resForFleetContainer.add(this.manpowerIconText);
        this.resForFleetContainer.visible = false;

        consumableResTitleHor = undefined;
        consumableResourceLineHeight = -(this.popupBackground.height * this.popupBackground.scaleY * 0.4);
        var fleetLOR = [];
        fleetLOR.push(this.addConsumableResourceLine('assault_cruiser', 'Assault Cruiser', 25, 28, widthLOR));
        fleetLOR.push(this.addConsumableResourceLine('battlecruiser', 'Battlecruiser', 12, 25, widthLOR));
        fleetLOR.push(this.addConsumableResourceLine('light_cruiser', 'Light Cruiser', 7, 15, widthLOR));
        fleetLOR.push(this.addConsumableResourceLine('heavy_transport', 'Heavy Transport', 0, 0, widthLOR));
        fleetLOR.push(this.addConsumableResourceLine('heavy_cruiser', 'Heavy Cruiser', 8, 18, widthLOR));
        fleetLOR.push(this.addConsumableResourceLine('frigate', 'Frigate', 5, 10, widthLOR));
        fleetLOR.push(this.addConsumableResourceLine('carrier', 'Carrier', 12, 28, widthLOR));

        for (var i = 0; i < fleetLOR.length; i++) this.resForFleetContainer.add(fleetLOR[i]);

        // Finalize popup window
        this.popupWindow.add(this.popupBackground);
        this.popupWindow.add(this.popupClose);
        this.popupWindow.add(this.popupCloseText);
        this.popupWindow.add(this.popupTitleBar);
        this.popupWindow.add(this.popupTitle);
        this.popupWindow.add(this.popupDescription);
        this.popupWindow.add(this.popupErrorMsg);
        //this.popupWindow.add(this.popupAddButton);
        //this.popupWindow.add(this.popupAddButtonText);
        // this.popupWindow.add(this.unitsIcon);
        // this.popupWindow.add(this.unitsIconText);
        this.popupWindow.add(this.resForArmyContainer);
        this.popupWindow.add(this.resForFleetContainer);

        this.popupWindow.visible = false;

        this.popupWindow.setScrollFactor(0);

        //

        // Fullscreen button
        this.isFullScreen = false;
        this.fullscreenButton = this.add.image(this.game.config.width - 30, this.game.config.height * 0.5, 'quad');
        this.fullscreenButton.setInteractive();
        this.fullscreenButton.setScrollFactor(0);
        this.fullscreenButton.setScale(1.2);
        this.fullscreenButton.on('pointerdown', function (pointer) {
          if (this.scale.isFullscreen) {
            this.scale.stopFullscreen();
          }
          else {
            this.scale.startFullscreen();
          }
        }, this);

        this.input.keyboard.on('keydown-' + 'ESC', function (event) {
          this.fullscreenButton.visible = true;
        }, this);

        this.selectedInputCoordinate = null;
        this.inputCoordinateWasSelected = false;

        this.inputTextXPos = { x: this.game.config.width * 0.015, y: this.game.config.height * 0.15 };
        this.inputTextYPos = { x: this.game.config.width * 0.015, y: this.game.config.height * 0.20 };

        this.inputTextXLabel = this.add.image(this.inputTextXPos.x, this.inputTextXPos.y, 'quad');
        this.inputTextXLabel.setOrigin(0, 0.5);
        this.inputTextXLabel.setInteractive();
        this.inputTextXLabel.setScrollFactor(0);
        this.inputTextXLabel.setScale(0.4 * 10, 0.8);
        this.inputTextXLabel.setTint(0xc0c0c0);

        this.inputTextX = this.add.text(this.inputTextXPos.x, this.inputTextXPos.y, "0", { fontFamily: 'Arial', fontSize: 20, color: '#000000' });
        this.inputTextX.setOrigin(0, 0.5);
        this.inputTextX.setScrollFactor(0);
        this.inputTextX.label = this.inputTextXLabel;
        this.inputTextXLabel.on('pointerdown', function (pointer) {
          if (this.scene.selectedInputCoordinate != null)
            this.scene.selectedInputCoordinate.label.setTint(0xc0c0c0);

          this.clearTint();

          this.scene.selectedInputCoordinate = this.scene.inputTextX;
          this.scene.inputCoordinateWasSelected = true;
        });

        this.inputTextYLabel = this.add.image(this.inputTextYPos.x, this.inputTextYPos.y, 'quad');
        this.inputTextYLabel.setOrigin(0, 0.5);
        this.inputTextYLabel.setInteractive();
        this.inputTextYLabel.setScrollFactor(0);
        this.inputTextYLabel.setScale(0.4 * 10, 0.8);
        this.inputTextYLabel.setTint(0xc0c0c0);

        this.inputTextY = this.add.text(this.inputTextYPos.x, this.inputTextYPos.y, "0", { fontFamily: 'Arial', fontSize: 20, color: '#000000' });
        this.inputTextY.setOrigin(0, 0.5);
        this.inputTextY.setScrollFactor(0);
        this.inputTextY.label = this.inputTextYLabel;
        this.inputTextYLabel.on('pointerdown', function (pointer) {
          if (this.scene.selectedInputCoordinate != null)
            this.scene.selectedInputCoordinate.label.setTint(0xc0c0c0);

          this.clearTint();

          this.scene.selectedInputCoordinate = this.scene.inputTextY;
          this.scene.inputCoordinateWasSelected = true;
        });

        this.coordinateLabelsContainer = this.add.container(0, 0);
        this.coordinateLabelsContainer.add(this.inputTextXLabel);
        this.coordinateLabelsContainer.add(this.inputTextX);
        this.coordinateLabelsContainer.add(this.inputTextYLabel);
        this.coordinateLabelsContainer.add(this.inputTextY);

        this.input.keyboard.on('keydown', function (eventName, event) {
          var key = eventName.key;
          if (this.selectedInputCoordinate != null) {
            if (eventName.keyCode == 8) {
              if (this.selectedInputCoordinate.text.length > 0) {
                this.selectedInputCoordinate.text = this.selectedInputCoordinate.text.substring(0, this.selectedInputCoordinate.text.length - 1);
              }
              return;
            }

            if (!isNaN(key) && this.selectedInputCoordinate.text.length < 10) {
              this.selectedInputCoordinate.text = this.selectedInputCoordinate.text + key;
            }
          }
        }, this);
      },

      update: function (time, delta) {
        let ClockNumber = 0;
        let ClockLabel = 'Day';
        this.KeyBoardKeys.update(delta);
        this.avatarPlanet.visible = this.movablePlanet.visible;
        this.avatarPlanet.x = this.movablePlanet.x;
        this.avatarPlanet.y = this.movablePlanet.y;

        this.movablePlanetArc.x = this.movablePlanet.x;
        this.movablePlanetArc.y = this.movablePlanet.y;

        this.movablePlanetLabel.visible = this.movablePlanet.visible;
        this.movablePlanetLabel.x = this.movablePlanet.x;
        this.movablePlanetLabel.y = this.movablePlanet.y + this.movablePlanetLabel.height * 2.8;

        this.planetArmyUnitLabel.visible = this.movablePlanet.visible;
        this.planetArmyUnitBorder.visible = this.movablePlanet.visible;

        this.planetFleetUnitLabel.visible = this.movablePlanet.visible;
        this.planetFleetUnitBorder.visible = this.movablePlanet.visible;

        let armyAmount = localStorage.getItem('ArmayAmount');
        let fleetAmount = localStorage.getItem('FleetAmount');
        if (armyAmount != undefined) {
          this.planetArmyUnitBorder2.visible = this.movablePlanet.visible && parseInt(armyAmount) > 1;
        }
        if (fleetAmount) {
          this.planetFleetUnitBorder2.visible = this.movablePlanet.visible && parseInt(fleetAmount) > 1;
        }

        if (this.planetArmyUnitLabel.visible) {
          var pos = { x: this.movablePlanet.x, y: this.movablePlanet.y };
          pos.y -= this.movablePlanet.height * 0.8;

          this.planetArmyUnitLabel.setPosition(pos.x, pos.y);
          this.planetArmyUnitBorder.setPosition(pos.x, pos.y);

          this.planetArmyUnitBorder2.setPosition(pos.x + this.planetArmyUnitBorder2.width * 0.12, pos.y - this.planetArmyUnitBorder2.height * 0.16);

          this.disembarkButtonContainer.setPosition(pos.x + this.disembarkButton.width * 0.1, pos.y - this.disembarkButton.height * 0.5);

          pos.y += (this.movablePlanet.height * 0.8) * 2.5;

          this.planetFleetUnitLabel.setPosition(pos.x, pos.y);
          this.planetFleetUnitBorder.setPosition(pos.x, pos.y);

          this.planetFleetUnitBorder2.setPosition(pos.x + this.planetFleetUnitBorder2.width * 0.02, pos.y - this.planetFleetUnitBorder2.height * 0.175);

          let armyAmount = localStorage.getItem('ArmayAmount');
          let fleetAmount = localStorage.getItem('FleetAmount');
          this.planetArmyUnitLabel.text = "" + armyAmount;
          this.planetFleetUnitLabel.text = "" + fleetAmount;

        }

        this.clockLabel.text = "" + ClockNumber + " " + ClockLabel;

        this._server.sendToServer('move', this.movablePlanet.x, this.movablePlanet.y, 0, '');
      }
    }
  }

  ngOnInit() {
    localStorage.setItem("isGameLoad", "true");
  }

  setPlanetPosition(planet, planets, lines, camera): void {
    camera.centerOn(planet.x, planet.y);
 
    var planetPos = new Phaser.Math.Vector2(planet.x, planet.y);
    
    function getNearestPlanet(ignoredPlanet = null)
    {
        var lastLength1 = 100000;
        var closestOne = null;

        for(var i = 0; i < planets.length; i++)
        {
            var otherPlanet = planets[i];
            if(otherPlanet == ignoredPlanet) continue;

            var otherPlanetPos = new Phaser.Math.Vector2(otherPlanet.x, otherPlanet.y);

            var r = otherPlanetPos.subtract(planetPos);
            var vecLength = r.length();

            if(vecLength < lastLength1)
            {
                closestOne = otherPlanet;
                lastLength1 = vecLength;
            }
        }

        return closestOne;
    }

    var closestPlanet1 = getNearestPlanet();
    var closestPlanet2 = getNearestPlanet(closestPlanet1);

    lines[0].setTo(planet.x, planet.y, closestPlanet1.x, closestPlanet1.y);
    lines[1].setTo(planet.x, planet.y, closestPlanet2.x, closestPlanet2.y);

    planet.visible = true;
    planet.created = true;
  }

  onAddUnitsAttempt(this): void {
    var hasResources = this.moneyLabel.amount >= this.requiredMoney && this.manPower.amount >= this.requiredManPower && this.alloys.amount >= this.requiredAlloy;

    if(!hasResources) { this.popupErrorMsg.visible = true; }
    else 
    {
        this.setLabelAmount(this.moneyLabel, this.moneyLabel.amount - this.requiredMoney);
        this.setLabelAmount(this.manPower, this.manPower.amount - this.requiredManPower);
        this.setLabelAmount(this.alloys, this.alloys.amount - this.requiredAlloy);

        if(this.activeUnit == 'Army')
        {
          let armyAmount = localStorage.getItem('ArmayAmount');
          let sum = parseInt(armyAmount);
          sum++;
          localStorage.setItem('ArmayAmount', sum.toString());
            this.refreshArmyUnits.call(this);
        }
        else if(this.activeUnit == 'Fleet')
        {
          let fleetAmount = localStorage.getItem('FleetAmount');
          let sum = parseInt(fleetAmount);
          sum++;
          localStorage.setItem('FleetAmount', sum.toString());
            this.refreshFleetUnits.call(this);
        }
    }
  }

  refreshArmyUnits(this): void {
    this.activeUnit = 'Army';
    let armyAmount = localStorage.getItem('ArmayAmount');
    this.armyUnitLabel.text = this.activeUnit + ' Unit: ' + armyAmount;
    //this.unitsIcon.setTexture('rect');
    //this.unitsIconText.text = ArmyAmount;
    this.resForArmyContainer.visible = true;
  }

  refreshFleetUnits(this): void {
    this.activeUnit = 'Fleet';
    let fleetAmount = localStorage.getItem('FleetAmount');
    this.armyUnitLabel.text = this.activeUnit + ' Unit: ' + fleetAmount;
    //this.unitsIcon.setTexture('pent');
    //this.unitsIconText.text = FleetAmount;
    this.resForFleetContainer.visible = true;
  }

  setLabelAmount(label, amount) {
    label.amount = amount;
    label.label.text = "" + label.amount;
  }
}
