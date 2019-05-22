'use strict';


customElements.define('compodoc-menu', class extends HTMLElement {
    constructor() {
        super();
        this.isNormalMode = this.getAttribute('mode') === 'normal';
    }

    connectedCallback() {
        this.render(this.isNormalMode);
    }

    render(isNormalMode) {
        let tp = lithtml.html(`
        <nav>
            <ul class="list">
                <li class="title">
                    <a href="index.html" data-type="index-link">galaxy-at-war documentation</a>
                </li>

                <li class="divider"></li>
                ${ isNormalMode ? `<div id="book-search-input" role="search"><input type="text" placeholder="Type to search"></div>` : '' }
                <li class="chapter">
                    <a data-type="chapter-link" href="index.html"><span class="icon ion-ios-home"></span>Getting started</a>
                    <ul class="links">
                        <li class="link">
                            <a href="overview.html" data-type="chapter-link">
                                <span class="icon ion-ios-keypad"></span>Overview
                            </a>
                        </li>
                        <li class="link">
                            <a href="index.html" data-type="chapter-link">
                                <span class="icon ion-ios-paper"></span>README
                            </a>
                        </li>
                        <li class="link">
                            <a href="dependencies.html" data-type="chapter-link">
                                <span class="icon ion-ios-list"></span>Dependencies
                            </a>
                        </li>
                    </ul>
                </li>
                    <li class="chapter modules">
                        <a data-type="chapter-link" href="modules.html">
                            <div class="menu-toggler linked" data-toggle="collapse" ${ isNormalMode ?
                                'data-target="#modules-links"' : 'data-target="#xs-modules-links"' }>
                                <span class="icon ion-ios-archive"></span>
                                <span class="link-name">Modules</span>
                                <span class="icon ion-ios-arrow-down"></span>
                            </div>
                        </a>
                        <ul class="links collapse" ${ isNormalMode ? 'id="modules-links"' : 'id="xs-modules-links"' }>
                            <li class="link">
                                <a href="modules/AppModule.html" data-type="entity-link">AppModule</a>
                                    <li class="chapter inner">
                                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                            'data-target="#components-links-module-AppModule-855178ff1de9c0acfde171ea46cb40f7"' : 'data-target="#xs-components-links-module-AppModule-855178ff1de9c0acfde171ea46cb40f7"' }>
                                            <span class="icon ion-md-cog"></span>
                                            <span>Components</span>
                                            <span class="icon ion-ios-arrow-down"></span>
                                        </div>
                                        <ul class="links collapse" ${ isNormalMode ? 'id="components-links-module-AppModule-855178ff1de9c0acfde171ea46cb40f7"' :
                                            'id="xs-components-links-module-AppModule-855178ff1de9c0acfde171ea46cb40f7"' }>
                                            <li class="link">
                                                <a href="components/AppComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">AppComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/ChatComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">ChatComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/CreateAccountComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">CreateAccountComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/GameConfigLobbyComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">GameConfigLobbyComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/GameDashboardComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">GameDashboardComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/GamePregameComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">GamePregameComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/ProfileComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">ProfileComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/SigninComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">SigninComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/SocketsComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">SocketsComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/gameDiscoveryLobbyComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">gameDiscoveryLobbyComponent</a>
                                            </li>
                                        </ul>
                                    </li>
                                <li class="chapter inner">
                                    <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                        'data-target="#injectables-links-module-AppModule-855178ff1de9c0acfde171ea46cb40f7"' : 'data-target="#xs-injectables-links-module-AppModule-855178ff1de9c0acfde171ea46cb40f7"' }>
                                        <span class="icon ion-md-arrow-round-down"></span>
                                        <span>Injectables</span>
                                        <span class="icon ion-ios-arrow-down"></span>
                                    </div>
                                    <ul class="links collapse" ${ isNormalMode ? 'id="injectables-links-module-AppModule-855178ff1de9c0acfde171ea46cb40f7"' :
                                        'id="xs-injectables-links-module-AppModule-855178ff1de9c0acfde171ea46cb40f7"' }>
                                        <li class="link">
                                            <a href="injectables/GameConfigService.html"
                                                data-type="entity-link" data-context="sub-entity" data-context-id="modules" }>GameConfigService</a>
                                        </li>
                                        <li class="link">
                                            <a href="injectables/LoginAuthenticatorService.html"
                                                data-type="entity-link" data-context="sub-entity" data-context-id="modules" }>LoginAuthenticatorService</a>
                                        </li>
                                        <li class="link">
                                            <a href="injectables/ProfileUpdaterService.html"
                                                data-type="entity-link" data-context="sub-entity" data-context-id="modules" }>ProfileUpdaterService</a>
                                        </li>
                                        <li class="link">
                                            <a href="injectables/UserSessionService.html"
                                                data-type="entity-link" data-context="sub-entity" data-context-id="modules" }>UserSessionService</a>
                                        </li>
                                    </ul>
                                </li>
                            </li>
                            <li class="link">
                                <a href="modules/AppRoutingModule.html" data-type="entity-link">AppRoutingModule</a>
                            </li>
                            <li class="link">
                                <a href="modules/CreateAccountModule.html" data-type="entity-link">CreateAccountModule</a>
                            </li>
                            <li class="link">
                                <a href="modules/gameConfigLobbyModule.html" data-type="entity-link">gameConfigLobbyModule</a>
                            </li>
                            <li class="link">
                                <a href="modules/gameDiscoveryLobbyModule.html" data-type="entity-link">gameDiscoveryLobbyModule</a>
                            </li>
                            <li class="link">
                                <a href="modules/gameDiscoveryLobbyModule.html" data-type="entity-link">gameDiscoveryLobbyModule</a>
                            </li>
                            <li class="link">
                                <a href="modules/ProfileModule.html" data-type="entity-link">ProfileModule</a>
                            </li>
                            <li class="link">
                                <a href="modules/SigninModule.html" data-type="entity-link">SigninModule</a>
                            </li>
                            <li class="link">
                                <a href="modules/SocketsModule.html" data-type="entity-link">SocketsModule</a>
                                    <li class="chapter inner">
                                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                            'data-target="#components-links-module-SocketsModule-718002eaee3160fa23769119c42c8cce"' : 'data-target="#xs-components-links-module-SocketsModule-718002eaee3160fa23769119c42c8cce"' }>
                                            <span class="icon ion-md-cog"></span>
                                            <span>Components</span>
                                            <span class="icon ion-ios-arrow-down"></span>
                                        </div>
                                        <ul class="links collapse" ${ isNormalMode ? 'id="components-links-module-SocketsModule-718002eaee3160fa23769119c42c8cce"' :
                                            'id="xs-components-links-module-SocketsModule-718002eaee3160fa23769119c42c8cce"' }>
                                            <li class="link">
                                                <a href="components/SocketsComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">SocketsComponent</a>
                                            </li>
                                        </ul>
                                    </li>
                            </li>
                </ul>
                </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ? 'data-target="#classes-links"' :
                            'data-target="#xs-classes-links"' }>
                            <span class="icon ion-ios-paper"></span>
                            <span>Classes</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse" ${ isNormalMode ? 'id="classes-links"' : 'id="xs-classes-links"' }>
                            <li class="link">
                                <a href="classes/Helper.html" data-type="entity-link">Helper</a>
                            </li>
                            <li class="link">
                                <a href="classes/Message.html" data-type="entity-link">Message</a>
                            </li>
                            <li class="link">
                                <a href="classes/User.html" data-type="entity-link">User</a>
                            </li>
                        </ul>
                    </li>
                        <li class="chapter">
                            <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ? 'data-target="#injectables-links"' :
                                'data-target="#xs-injectables-links"' }>
                                <span class="icon ion-md-arrow-round-down"></span>
                                <span>Injectables</span>
                                <span class="icon ion-ios-arrow-down"></span>
                            </div>
                            <ul class="links collapse" ${ isNormalMode ? 'id="injectables-links"' : 'id="xs-injectables-links"' }>
                                <li class="link">
                                    <a href="injectables/ChatService.html" data-type="entity-link">ChatService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/GameUpdaterServiceService.html" data-type="entity-link">GameUpdaterServiceService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/UserService.html" data-type="entity-link">UserService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/XmlhttpService.html" data-type="entity-link">XmlhttpService</a>
                                </li>
                            </ul>
                        </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ? 'data-target="#interfaces-links"' :
                            'data-target="#xs-interfaces-links"' }>
                            <span class="icon ion-md-information-circle-outline"></span>
                            <span>Interfaces</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse" ${ isNormalMode ? ' id="interfaces-links"' : 'id="xs-interfaces-links"' }>
                            <li class="link">
                                <a href="interfaces/IGameConfig.html" data-type="entity-link">IGameConfig</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/IGameConfigModel.html" data-type="entity-link">IGameConfigModel</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ILoginAuthenticator.html" data-type="entity-link">ILoginAuthenticator</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/IProfileModel.html" data-type="entity-link">IProfileModel</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/IProfileUpdater.html" data-type="entity-link">IProfileUpdater</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/IUserAccountFactory.html" data-type="entity-link">IUserAccountFactory</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/LoginModel.html" data-type="entity-link">LoginModel</a>
                            </li>
                        </ul>
                    </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ? 'data-target="#miscellaneous-links"'
                            : 'data-target="#xs-miscellaneous-links"' }>
                            <span class="icon ion-ios-cube"></span>
                            <span>Miscellaneous</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse" ${ isNormalMode ? 'id="miscellaneous-links"' : 'id="xs-miscellaneous-links"' }>
                            <li class="link">
                                <a href="miscellaneous/enumerations.html" data-type="entity-link">Enums</a>
                            </li>
                            <li class="link">
                                <a href="miscellaneous/variables.html" data-type="entity-link">Variables</a>
                            </li>
                        </ul>
                    </li>
                        <li class="chapter">
                            <a data-type="chapter-link" href="routes.html"><span class="icon ion-ios-git-branch"></span>Routes</a>
                        </li>
                    <li class="chapter">
                        <a data-type="chapter-link" href="coverage.html"><span class="icon ion-ios-stats"></span>Documentation coverage</a>
                    </li>
                    <li class="divider"></li>
                    <li class="copyright">
                        Documentation generated using <a href="https://compodoc.app/" target="_blank">
                            <img data-src="images/compodoc-vectorise.png" class="img-responsive" data-type="compodoc-logo">
                        </a>
                    </li>
            </ul>
        </nav>
        `);
        this.innerHTML = tp.strings;
    }
});