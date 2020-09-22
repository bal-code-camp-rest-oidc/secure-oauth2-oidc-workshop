import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, ReplaySubject} from "rxjs";
import {AuthConfig, NullValidationHandler, OAuthService} from "angular-oauth2-oidc";
import {Router} from "@angular/router";
import {map} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
// @ts-ignore
export class AuthService {
  private authConfig: AuthConfig = {

    // Url of the Identity Provider
    issuer: 'http://localhost:8080/auth/realms/workshop',

    // URL of the SPA to redirect the user to after login
    redirectUri: window.location.origin + '/index.html',

    // The SPA's id. The SPA is registered with this id at the auth-server
    clientId: 'spa',

    responseType: 'code',
    disableAtHashCheck: true,

    // set the scope for the permissions the client should request
    // The first three are defined by OIDC. The 4th is a use case specific one
    scope: 'openid profile'
  }

  private isAuthenticatedSubject$ = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject$.asObservable();

  private isDoneLoadingSubject$ = new ReplaySubject<boolean>();
  public isDoneLoading$ = this.isDoneLoadingSubject$.asObservable();

  constructor(private oauthService: OAuthService,
              private router: Router
  ) {
    this.oauthService.configure(this.authConfig);
    this.oauthService.tokenValidationHandler = new NullValidationHandler();

    this.oauthService.events
      .subscribe(_ => {
        this.isAuthenticatedSubject$.next(this.oauthService.hasValidAccessToken());
      });

    this.oauthService.setupAutomaticSilentRefresh();
  }

  /**
   * Publishes `true` if and only if (a) all the asynchronous initial
   * login calls have completed or errored, and (b) the user ended up
   * being authenticated.
   *
   * In essence, it combines:
   *
   * - the latest known state of whether the user is authorized
   * - whether the ajax calls for initial log in have all been done
   */
  public canActivateProtectedRoutes$: Observable<boolean> = combineLatest(
    this.isAuthenticated$,
    this.isDoneLoading$
  ).pipe(map(values => values.every(b => b)));

  public runInitialLoginSequence(): Promise<void> {
    return this.oauthService.loadDiscoveryDocument()
      .then(() => this.oauthService.tryLogin())
      .then(() => {
        this.isDoneLoadingSubject$.next(true);
        // remove query params
        this.router.navigate(['']);
      })
      .catch(() => this.isDoneLoadingSubject$.next(true));
  }

  public hasRole(role: string) {
    let claims: any = this.oauthService.getIdentityClaims();
    if (claims && claims.groups) {
      let roles: string[] = claims.groups;
      roles = roles.map(role => role.toUpperCase());
      return roles.includes(role.toLocaleUpperCase());
    }
    return false;
  }

  public getFullname() {
    let claims: any = this.oauthService.getIdentityClaims();
    if (claims && claims.name) {
      return claims.name;
    }
    return undefined;
  }

  public login() {
    this.oauthService.initCodeFlow();
  }

  public logout() {
    this.oauthService.logOut();
  }

  public refresh() {
    this.oauthService.silentRefresh();
  }

  public hasValidToken() {
    return this.oauthService.hasValidAccessToken();
  }
}
